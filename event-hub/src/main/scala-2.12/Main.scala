import Client.PushMessage
import ClientHandler.Notify
import akka.NotUsed
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Flow, Sink, Source}
import model.ActionEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import spray.json.JsonParser
import model.JsonFormats._

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Main extends App {
  implicit val system = ActorSystem("Subscription")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val clientHandler = system.actorOf(Props(new ClientHandler))

  Consumer.plainSource(consumerSettings, Subscriptions.topics("action-event"))
    .map(x => JsonParser(x.value()).convertTo[ActionEvent])
    .map(x => Notify(x.subId, s"Sync complete. SubId = ${x.subId}; wCount = ${x.result.wCount} message: ${x.result.errorMessage}"))
    .map(clientHandler ! _).runWith(Sink.ignore)

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

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8003)

  println(s"Server online at http://localhost:8003/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}


