package com.practice.recon

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import akka.util.Timeout._
import com.amazonaws.services.batch.model.JobDefinition
import com.practice.recon.job.recon.ReconActor
import com.practice.recon.job.recon.ReconJob.{ReconRequest, ReconResponse}
import org.apache.spark.sql.SparkSession


class ReconEngine extends Actor with ActorLogging {

  implicit def executionContext = context.dispatcher


  val spark = SparkSession.builder().appName("Reconciliation").master("local[*]").getOrCreate()

  override def receive = {
    case JobDefinition(jobName, paramMap) =>
      val job = JobDefinition(jobName, paramMap)
      implicit val timeout = Timeout(5, TimeUnit.SECONDS)

      job.name match {
        case "recon" =>
          println("Reconciliation job")
          val reconActor = context.actorOf(props = Props[ReconActor])
          pipe(reconActor.ask(ReconRequest(job, spark)).mapTo[ReconResponse]) to sender()

        case _ =>
      }
  }

}