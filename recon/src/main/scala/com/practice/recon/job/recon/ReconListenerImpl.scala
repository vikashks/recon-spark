package com.practice.recon.job.recon

import com.practice.recon.antrl.ReconciliationParser.{ExprContext, JoinContext}
import com.practice.recon.antrl.{ReconciliationBaseListener, ReconciliationParser}
import org.antlr.v4.runtime.tree.{ParseTree, TerminalNodeImpl}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

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
      case "match" :: left :: "with" :: right :: Nil =>
        reconDagBuilder.addLeftDF(spark.read.option("header", "true").csv(s"file:///Users/vks/Workspace/goeuro/spark-jobs/$left.csv"))
        reconDagBuilder.addRightDF(spark.read.option("header", "true").csv(s"file:///Users/vks/Workspace/goeuro/spark-jobs/$right.csv"))
    }
  }

  /**
    * {@inheritDoc }
    * <p>
    * <p>The default implementation does nothing.</p>
    */
  override def enterExpr(ctx: ReconciliationParser.ExprContext): Unit = {
    import scala.collection.JavaConversions._
    val list = ctx.children.toList
    list.foreach((tree: ParseTree) => {
      tree match {
        case expr: ExprContext =>
          val list = expr.children.toList
          addWhereCriteria(list)
        case _: TerminalNodeImpl =>
          addWhereCriteria(list)
        case _ =>
      }
    })
  }

  private def addWhereCriteria(list: List[ParseTree]) = {
    val leftColumn = list.head.getText
    val operation = list(1).getText
    val rightColumn = list(2).getText
    operation match {
      case "=" =>
        val column = col(leftColumn).equalTo(col(rightColumn))
        reconDagBuilder.whereCriteria(column)
      case _ =>
    }
  }

  /**
    * {@inheritDoc }
    * <p>
    * <p>The default implementation does nothing.</p>
    */
  override def enterJoin(ctx: ReconciliationParser.JoinContext): Unit = {
    import scala.collection.JavaConversions._
    val list = ctx.children.toList
    list.foreach((tree: ParseTree) => {
      tree match {
        case expr: JoinContext =>
          val list = expr.children.toList
          addJoinCriteria(list)
        case _: TerminalNodeImpl =>
          addJoinCriteria(list)
        case _ =>
      }
    })
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

  /**
    * {@inheritDoc }
    * <p>
    * <p>The default implementation does nothing.</p>
    */
  override def enterOperation(ctx: ReconciliationParser.OperationContext): Unit = super.enterOperation(ctx)
}
