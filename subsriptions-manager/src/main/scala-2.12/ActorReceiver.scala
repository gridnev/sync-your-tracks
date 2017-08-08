import ActorReceiver.GetSubscription
import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.pattern.{ask, pipe}
import akka.util.ByteString
import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Denis Gridnev on 30.07.2017.
  */

case class Subscription(source: Source, target: Target)
case class Source(id: String/*, data:ConnectionData*/)
case class Target(id: String/*, data:ConnectionData*/)

class ActorReceiver(sender: ActorRef) extends Actor {


  implicit val t1 = jsonFormat1(Source)
  implicit val t2 = jsonFormat1(Target)
  implicit val colorFormat = jsonFormat2(Subscription)

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  context.system.scheduler.scheduleOnce(10 second, self, GetSubscription)

  override def receive: Receive = {
    case GetSubscription => {
      Http(context.system).singleRequest(HttpRequest(uri = "http://localhost:8001/subscriptions")) pipeTo self
      context.system.scheduler.scheduleOnce(10 second, self, GetSubscription)
    }

    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
        JsonParser(body.utf8String).convertTo[Seq[Subscription]].head
      } pipeTo sender

    case resp @ HttpResponse(code, _, _, _) =>
      resp.discardEntityBytes()
      println("error during receive")
  }
}

object ActorReceiver {
  case object GetSubscription
}
