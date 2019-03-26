package com.practice.recon.job.recon

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

import akka.actor.Actor
import com.practice.recon.JobDefinition
import com.practice.recon.antrl.{ReconciliationLexer, ReconciliationParser}
import com.practice.recon.job.recon.ReconJob.ReconRequest
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.{CodePointBuffer, CodePointCharStream, CommonTokenStream}
import org.apache.spark.sql.SparkSession

object ReconJob {


  case class ReconRequest(job: JobDefinition, spark: SparkSession)

  case class ReconResponse(response: Boolean)

  def execute(job: JobDefinition, spark: SparkSession): Boolean = {
    val param = job.param

    val optionalReconRule = param.get("recon_rule")
    val allBytes = optionalReconRule match {
      case Some(rule) => rule.getBytes
      case _ => Files.readAllBytes(Paths.get("recon.txt"))
    }

    val buffer = CodePointBuffer.withBytes(ByteBuffer.wrap(allBytes))
    val codePointCharStream = CodePointCharStream.fromBuffer(buffer)

    val lexer = new ReconciliationLexer(codePointCharStream)

    val tokens = new CommonTokenStream(lexer)
    val parser = new ReconciliationParser(tokens)
    val walker = new ParseTreeWalker
    val dagBuilder = new ReconDagBuilder
    walker.walk(new ReconListenerImpl(spark, dagBuilder), parser.`def`)

    val dataFrame = dagBuilder.build()

    dataFrame.show(10, false)

    true
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Reconciliation").master("local[*]").getOrCreate()
    execute(JobDefinition("se_stat", Map.empty), spark)
  }

}


class ReconActor extends Actor with Serializable {

  override def receive = {
    case ReconRequest(job, spark) =>
      ReconJob.execute(job, spark)
  }
}
