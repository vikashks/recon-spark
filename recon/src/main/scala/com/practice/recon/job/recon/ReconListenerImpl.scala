package com.practice.recon.job.recon

import com.practice.recon.antrl.ReconciliationParser.{JoinContext, OperationContext, OperatorContext}
import com.practice.recon.antrl.{ReconciliationBaseListener, ReconciliationParser}
import org.antlr.v4.runtime.tree.{ParseTree, TerminalNodeImpl}
import org.apache.spark.sql.functions.{col, sum}
import org.apache.spark.sql.{Column, SparkSession}

case class ReconListenerImpl(spark: SparkSession, reconDagBuilder: ReconDagBuilder) extends ReconciliationBaseListener {
  /**
    * {@inheritDoc }
    * <p>
    * <p>The default implementation does nothing.</p>
    */
  override def enterSource(ctx: ReconciliationParser.SourceContext): Unit = {
    import scala.collection.JavaConversions._
    val nodes = ctx.children.toList.map(_.getText)
    nodes match {
      case "match" :: left :: leftFormat :: "with" :: right :: rightFormat :: Nil =>
        reconDagBuilder.addLeftDF(getDataframe(left, leftFormat))
        reconDagBuilder.addRightDF(getDataframe(right, rightFormat))
    }
  }

  private def getDataframe(alias: String, leftFormat: String) = {
    val parenthesisStartIndex = leftFormat.indexOf("(")
    val parenthesisEndIndex = leftFormat.indexOf(")")
    val source = leftFormat.substring(0, parenthesisStartIndex)
    val dataFrame = source match {
      case "csv" =>
        val path = leftFormat.substring(parenthesisStartIndex + 1, parenthesisEndIndex)
        spark.read.option("header", "true").option("inferSchema", "true").csv(path)
      case "parquet" =>
        val path = leftFormat.substring(parenthesisStartIndex + 1, parenthesisEndIndex)
        spark.read.option("header", "true").option("inferSchema", "true").parquet(path)
    }
    /*val aliasColumns = dataFrame.columns.map((name: String) => col(name).alias(alias + "_" + name))
    dataFrame.select(aliasColumns: _*)*/
    dataFrame
  }

  /**
    * {@inheritDoc }
    *
    * <p>The default implementation does nothing.</p>
    */
  override def enterOperator(ctx: ReconciliationParser.OperatorContext): Unit = {
    import scala.collection.JavaConversions._
    val parent = ctx.getParent
    parent.children.toList match {
      case List(x: OperationContext, y: OperatorContext, z: OperationContext) =>
        val leftColumn: Column = getColumn(x)
        val rightColumn: Column = getColumn(z)
        y.getText match {
          case "=" =>
            reconDagBuilder.whereCriteria(leftColumn.equalTo(rightColumn))
        }
      case List(x: TerminalNodeImpl, y: OperatorContext, z: TerminalNodeImpl) =>
        y.getText match {
          case "=" if parent.isInstanceOf[JoinContext] =>
            reconDagBuilder.joinCriteria(getColumn(x).equalTo(getColumn(z)))
          case "=" =>
            reconDagBuilder.whereCriteria(getColumn(x).equalTo(getColumn(z)))
        }
    }
  }

  private def getColumn(node: TerminalNodeImpl) = {
    col(node.getText)
  }

  private def getColumn(context: OperationContext) = {
    val operationText = context.getText
    val parenthesisStartIndex = operationText.indexOf("(")
    val parenthesisEndIndex = operationText.indexOf(")")
    val operation = operationText.substring(0, parenthesisStartIndex).toLowerCase
    val columnName = operationText.substring(parenthesisStartIndex + 1, parenthesisEndIndex)
    val column = operation match {
      case "sum" => sum(col(columnName))
    }
    column
  }

  /**
    * {@inheritDoc }
    *
    * <p>The default implementation does nothing.</p>
    */
  override def enterGroup(ctx: ReconciliationParser.GroupContext): Unit = {
    import scala.collection.JavaConversions._
    val nodes = ctx.children.toList
    nodes match {
      case List(_: TerminalNodeImpl, node: TerminalNodeImpl) =>
        reconDagBuilder.groupCriteria(col(node.getText))
    }
  }

  private def addJoinCriteria(list: List[ParseTree]) = {
    val leftColumn = list.head.getText
    val operation = list(1).getText
    val rightColumn = list(2).getText
    operation match {
      case "=" =>
        val column = col(leftColumn).equalTo(col(rightColumn))
        reconDagBuilder.joinCriteria(column)
      case _ =>
    }
  }
}
