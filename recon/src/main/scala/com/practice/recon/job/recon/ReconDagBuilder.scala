package com.practice.recon.job.recon

import org.apache.spark.sql.{Column, DataFrame}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

class ReconDagBuilder {

  private var left: DataFrame = _

  private var right: DataFrame = _

  private var whereCriteria: List[Column] = Nil

  private var joinCriteria: List[Column] = Nil

  private var joinColumn: Column = _


  private var dataFrame: DataFrame = _

  def addLeftDF(left: DataFrame): ReconDagBuilder = {
    this.left = left
    this
  }

  def addRightDF(right: DataFrame): ReconDagBuilder = {
    this.right = right
    this
  }

  def whereCriteria(col: Column): ReconDagBuilder = {
    whereCriteria = col :: whereCriteria
    this
  }

  def joinCriteria(col: Column): ReconDagBuilder = {
    joinCriteria = col :: joinCriteria
    this
  }

  def build(): DataFrame = {
    dataFrame = left.join(right, temp(joinCriteria, null))
    whereCriteria.foreach((column: Column) => dataFrame.where(column))
    dataFrame
  }

  @tailrec
  private def temp(joinCriteria: List[Column], column: Column): Column = {
    joinCriteria match {
      case Nil =>
        column
      case col :: Nil =>
        temp(Nil, col)
      case col :: tail =>
        temp(tail, col && column)
    }
  }
}
