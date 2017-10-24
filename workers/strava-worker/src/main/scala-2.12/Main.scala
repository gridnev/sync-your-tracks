import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream._
import client.StravaClient
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import spray.json._
import akka.stream.scaladsl._
import model.JsonFormats._
import model.Task
import DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
object Main extends App {
  implicit val system = ActorSystem("kafka")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val client = new StravaClient(host = "localhost", port = 8001)
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
