import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import client.{StravaClient, Workout}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
object Main extends App {

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

  val client = new StravaClient(host, port)

  implicit val system = ActorSystem("kafka")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()


  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  Consumer.plainSource(consumerSettings, Subscriptions.topics("strava"))
    .mapAsync(1) { msg =>
      val task = JsonParser(msg.value).convertTo[Task]
      client.getWorkouts() map {
        workouts =>
          val nextTask = task.copy(workouts = Some(workouts))
          new ProducerRecord[Array[Byte], String](task.target.id, nextTask.toJson.compactPrint)
      }
    }.runWith(Producer.plainSink(producerSettings))
}
