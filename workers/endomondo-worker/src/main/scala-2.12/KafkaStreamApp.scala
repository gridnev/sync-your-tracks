import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import clients.EndomondoClient
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.ExecutionContext

object KafkaStreamApp extends App{
  /*implicit val system = ActorSystem("kafka")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val host = "api.mobile.endomondo.com"
  val client = new EndomondoClient(host)

  Consumer.committableSource(consumerSettings, Subscriptions.topics("test"))
    .mapAsync(1){ msg =>
      client.getWorkouts() map {
        ws => ProducerMessage.Message(new ProducerRecord[Array[Byte], String](
                "test2",
                ws.head.distance
              ), msg.committableOffset)
          }
      }.runWith(Producer.commitableSink(producerSettings))*/
}
