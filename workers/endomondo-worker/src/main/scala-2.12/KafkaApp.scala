import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.impl.VirtualProcessor.WrappedSubscription.PassThrough
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Denis Gridnev on 24.07.2017.
  */
object KafkaApp extends App {

  implicit val system = ActorSystem("kafka")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  Consumer.plainSource(consumerSettings, Subscriptions.topics("test"))
    .map { msg =>
      println(msg.value())
      msg
    }.map{
      msg =>
        ProducerMessage.Message(new ProducerRecord[Array[Byte], String]("test", ""))
    }
    .runWith(Producer.plainSink(producerSettings))
}

class Task(source: Source, target: Target)
class Source(id: String, data:ConnectionData)
class ConnectionData()

class Target(id: String, data:String)
