import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import spray.json._
import akka.stream.scaladsl._
import model.JsonFormats._
import model.{ActionEvent, Task, TaskResult}
import DefaultJsonProtocol._
import clients.EndomondoClient

import scala.util.control.NonFatal

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
object Main extends App {
  implicit val system = ActorSystem("kafka")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val actionEventTopicName = "action-event"
  val client = new EndomondoClient(host = "localhost", port = 8001)
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

  val taskToLog = Flow[Task].map{
    task =>
      new ProducerRecord[Array[Byte], String](actionEventTopicName,
        ActionEvent(
          subId = task.id,
          result = TaskResult(
            success = true,
            wCount = Some(task.workouts.size))).toJson.compactPrint)
  }

  val taskToActionEvent = Flow[Task].mapAsync(1) { task =>
    client.postWorkouts(task.workouts.toJson.compactPrint).map { _ =>
      new ProducerRecord[Array[Byte], String](actionEventTopicName,
        ActionEvent(
          subId = task.id,
          result = TaskResult(
            success = true,
            wCount = Some(task.workouts.size))).toJson.compactPrint)
    }
      .recover {
        case NonFatal(e) => new ProducerRecord[Array[Byte], String](actionEventTopicName,
          ActionEvent(
            subId = task.id,
            result = TaskResult(
              success = false,
              errorMessage = Some(e.getMessage))).toJson.compactPrint)
      }
  }

  def splitter(task: Task) = if(task.workouts.isDefined) 1 else 0

  // @formatter:off
  RunnableGraph.fromGraph(GraphDSL.create() {
    implicit builder =>
      import GraphDSL.Implicits._

      //Source
      val A: Outlet[ConsumerRecord[Array[Byte], String]] =
        builder.add(Consumer.plainSource(consumerSettings, Subscriptions.topics("endomondo"))).out

      // Flows
      val B: FlowShape[ConsumerRecord[Array[Byte], String], Task] = builder.add(recordToTask)
      val C: FlowShape[Task, ProducerRecord[Array[Byte], String]] = builder.add(taskToRecord)
      val F: FlowShape[Task, ProducerRecord[Array[Byte], String]] = builder.add(taskToActionEvent)
      //val G: FlowShape[Task, ProducerRecord[Array[Byte], String]] = builder.add(taskToLog)
      //val D = builder.add(Sink.foreach(client.postWorkouts)).in
      val split = builder.add(Partition[Task](2, splitter))
      //val bcast = builder.add(Broadcast[Task](2))

      // Sinks
      val E: Inlet[ProducerRecord[Array[Byte], String]] = builder.add(Producer.plainSink(producerSettings)).in
      val E1: Inlet[ProducerRecord[Array[Byte], String]] = builder.add(Producer.plainSink(producerSettings)).in

      // Graph
      A ~> B ~> split ~> C ~> E //back to workers
                split ~> F ~> E1 //to target


      ClosedShape
  }).run()
  // @formatter:on
}