import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import api.JobEndpoint
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger

import scala.concurrent.Future
import scala.concurrent.duration._

object Main {
  @transient lazy val logger = Logger.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    val port = "3385"
    val host = "localhost"

    implicit val system = ActorSystem("Batch-Jobs-Actor")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(1000 seconds)

    val api = new JobEndpoint(system, logger).routes

    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port.toInt) //Starts the HTTP server

    val log = Logging(system.eventStream, "akka-spark")
    bindingFuture.map { serverBinding =>
      log.info(s"RestApi bound to ${serverBinding.localAddress} ")
    }.onFailure {
      case ex: Exception =>
        log.error(ex, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }
  }
}
