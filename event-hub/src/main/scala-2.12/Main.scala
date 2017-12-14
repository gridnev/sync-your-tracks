import Client.PushMessage
import ClientHandler.Notify
import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, Sink, Source}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Main extends App {
  implicit val system = ActorSystem("Subscription")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val clientHandler = system.actorOf(Props(new ClientHandler))

  def hub(id: String): Flow[Message, Message, _] = {
    val clientActor = system.actorOf(Props(new Client(clientHandler, id)))

    val outgoingMessages: Source[Message, NotUsed] =
      Source.actorRef[PushMessage](10, OverflowStrategy.fail)
        .mapMaterializedValue { outActor =>
          clientActor ! Client.Connected(outActor)
          NotUsed
        }
        .collect {
          case outMsg: PushMessage => TextMessage(outMsg.text)
        }

    Flow.fromSinkAndSource(Sink.ignore, outgoingMessages)
  }

  val route = get {
    pathPrefix("ws" / Segment) { id =>
      get {
        handleWebSocketMessages(hub(id))
      }
    } ~
    pathPrefix("notify" / Segment / Segment) { (id, text) =>
      get{
        complete {
          clientHandler ! Notify(id, text)
          "Ok"
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8002)

  println(s"Server online at http://localhost:8002/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}


