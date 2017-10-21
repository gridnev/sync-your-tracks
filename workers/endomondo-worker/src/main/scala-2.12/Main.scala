import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization._
import spray.json._
import DefaultJsonProtocol._
import akka.stream.scaladsl.Sink
import clients.EndomondoClient

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Denis Gridnev on 24.07.2017.
  */
object Main extends App {
  case class Workout(distance: String, elapsed_time: String, id: String)
  case class Task(source: Source, target: Target, workouts: Option[Seq[Workout]] = None)
  case class Source(id: String/*, data:ConnectionData*/)
  case class ConnectionData()

  case class Target(id: String/*, data:String*/)

  implicit val t1 = jsonFormat1(Source)
  implicit val t2 = jsonFormat1(Target)
  implicit val t3 = jsonFormat3(Workout)
  implicit val colorFormat = jsonFormat3(Task)

  val host = "localhost"
  val port = 8001

  val client = new EndomondoClient(host, port, "123", "123")

  implicit val system = ActorSystem("kafka")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  Consumer.plainSource(consumerSettings, Subscriptions.topics("endomondo"))
    .map { msg =>
      val task = JsonParser(msg.value).convertTo[Task]
      task.workouts.toJson.compactPrint
    }.runWith(Sink.foreach(client.postWorkouts))
}
