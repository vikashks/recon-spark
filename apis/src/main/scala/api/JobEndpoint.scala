package api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives.{as, entity, pathEndOrSingleSlash, pathPrefix, post, _}
import akka.util.Timeout
import com.practice.recon.{JobDefinition, ReconEngine}
import org.apache.log4j.Logger
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._

trait Marshallers extends DefaultJsonProtocol {
  implicit val jd = jsonFormat2(JobDefinition)
}

class JobEndpoint(system: ActorSystem, log: Logger) extends Marshallers {

  val routes = sparkJobs
  val reconEngine = system.actorOf(Props[ReconEngine], "recon-engine")
  implicit val timeout = Timeout(1000 seconds)

  def runSparkJob(request: JobDefinition) = {
    import akka.pattern.ask
    reconEngine ? request
  }

  def sparkJobs = pathPrefix("recon") {
    pathEndOrSingleSlash {
      post {
        entity(as[JobDefinition]) {
          request =>
            onSuccess(runSparkJob(request)) {
              case Some(true) =>
                log.info("Job Ran Successfully")
                complete(StatusCode.int2StatusCode(0))
              case Some(false) =>
                log.error("Job Failed Check Log for more details")
                complete(StatusCode.int2StatusCode(1))
            }
        }
      }
    }
  }

}
