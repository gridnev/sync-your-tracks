package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import serializers.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
class BaseClient(host: String, port: Int)(implicit system: ActorSystem) extends JsonSupport {
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val httpClient = Http().outgoingConnection(host, port)

  def sendAndReceiveAs[T: Manifest](httpRequest: HttpRequest): Future[T] =
    sendAndReceive(httpRequest, response => unmarshaller[T].apply(response.entity))

  def sendAndReceive[T](request: HttpRequest, f: HttpResponse => Future[T]): Future[T] =
    Source.single(request)
      .via(httpClient)
      .mapAsync(1) { response =>
        if (response.status.isSuccess || response.status == StatusCodes.NotFound) f(response)
        else Future.failed(new Exception(s"Request failed. Response was: $response"))
      }
      .runWith(Sink.head)
}
