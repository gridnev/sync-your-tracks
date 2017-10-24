import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FlowShape, Inlet}
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph}
import client.SubscriptionClient
import spray.json._
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import model.{Subscription, Task}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import model.JsonFormats._
import Utils._

/**
  * Created by Denis Gridnev on 06.08.2017.
  */
object Main extends App {
  implicit val system = ActorSystem("Subscription")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val subscriptionClient = new SubscriptionClient(host = "localhost", port = 8001)
  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val tickToSubscriptions = Flow[Unit].mapAsync(1)(_ => subscriptionClient.getSubscriptions())
  val SeqToSingle = Flow[Seq[Subscription]].mapConcat(toImmutable)
  val subscriptionToRecord = Flow[Subscription].map(sub =>
    new ProducerRecord[Array[Byte], String](sub.source.id, Task(sub.source, sub.target).toJson.compactPrint))

  // @formatter:off
  RunnableGraph.fromGraph(GraphDSL.create() {
    implicit builder =>
      import akka.stream.scaladsl.GraphDSL.Implicits._

      //Source
      val A =
        builder.add(akka.stream.scaladsl.Source.tick(initialDelay = 0 second, interval = 5 second, ())).out

      // Flows
      val B: FlowShape[Unit, Seq[Subscription]] = builder.add(tickToSubscriptions)
      val C: FlowShape[Seq[Subscription], Subscription] = builder.add(SeqToSingle)
      val D: FlowShape[Subscription, ProducerRecord[Array[Byte], String]] = builder.add(subscriptionToRecord)

      // Sinks
      val E: Inlet[ProducerRecord[Array[Byte], String]] = builder.add(Producer.plainSink(producerSettings)).in

      // Graph
      A ~> B ~> C ~> D ~> E

      ClosedShape
  }).run()
}
