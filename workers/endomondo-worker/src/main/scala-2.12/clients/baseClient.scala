package clients

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import serializers.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Denis Gridnev on 23.07.2017.
  */
class BaseClient(host: String) extends JsonSupport {
  implicit val system = ActorSystem("endomondo-worker")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val httpClient = Http().outgoingConnection(host)

  def sendAndReceiveAs[T: Manifest](httpRequest: HttpRequest): Future[T] =
    sendAndReceive(httpRequest, response => unmarshaller[T].apply(response.entity))

  private def sendAndReceive[T](request: HttpRequest, f: HttpResponse => Future[T]): Future[T] =
    Source.single(request)
      .via(httpClient)
      .mapAsync(1) { response =>
        if (response.status.isSuccess || response.status == StatusCodes.NotFound) f(response)
        else Future.failed(new Exception(s"Request failed. Response was: $response"))
      }
      .runWith(Sink.head)
}
