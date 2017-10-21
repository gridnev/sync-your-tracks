import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream._
import client.{StravaClient, Workout}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import spray.json._
import DefaultJsonProtocol._
import akka.stream.scaladsl._

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

  val recordToTask = Flow[ConsumerRecord[Array[Byte], String]].map(x => JsonParser(x.value).convertTo[Task])
  val taskToRecord = Flow[Task].mapAsync(1){
    task =>
      client.getWorkouts() map {
        workouts =>
          val nextTask = task.copy(workouts = Some(workouts))
          new ProducerRecord[Array[Byte], String](task.target.id, nextTask.toJson.compactPrint)
      }
  }

  val taskToString = Flow[Task].map(_.workouts.toJson.compactPrint)

  def splitter(task: Task) = if(task.workouts.isDefined) 1 else 0

  // @formatter:off
  RunnableGraph.fromGraph(GraphDSL.create() {
    implicit builder =>
      import GraphDSL.Implicits._

      //Source
      val A: Outlet[ConsumerRecord[Array[Byte], String]] =
        builder.add(Consumer.plainSource(consumerSettings, Subscriptions.topics("strava"))).out

      // Flows
      val B: FlowShape[ConsumerRecord[Array[Byte], String], Task] = builder.add(recordToTask)
      val C: FlowShape[Task, ProducerRecord[Array[Byte], String]] = builder.add(taskToRecord)
      val F: FlowShape[Task, String] = builder.add(taskToString)
      val split = builder.add(Partition[Task](2, splitter))

      // Sinks
      val D = builder.add(Sink.foreach(client.postWorkouts)).in
      val E: Inlet[ProducerRecord[Array[Byte], String]] = builder.add(Producer.plainSink(producerSettings)).in

      // Graph
      A ~> B ~> split ~> C ~> E
                split ~> F ~> D

      ClosedShape
  }).run()
  // @formatter:on
}
