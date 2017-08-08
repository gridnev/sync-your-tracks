import akka.actor.Actor
import akka.actor.Actor.Receive
import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import spray.json._
import DefaultJsonProtocol._

/**
  * Created by Denis Gridnev on 30.07.2017.
  */
class SenderActor extends Actor {

  case class Task(source: Source, target: Target)

  implicit val t1 = jsonFormat1(Source)
  implicit val t2 = jsonFormat1(Target)
  implicit val colorFormat = jsonFormat2(Task)

  val producer = KafkaProducer(
    Conf(new ByteArraySerializer(), new StringSerializer(), bootstrapServers = "localhost:9092")
  )

  override def receive: Receive = {
    case Subscription(source, target) => {
      val record = KafkaProducerRecord[Array[Byte], String](source.id, None, Task(source, target).toJson.compactPrint)
      producer.send(record)
    }

  }
}
