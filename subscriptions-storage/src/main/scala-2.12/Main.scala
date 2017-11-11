import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import controllers.SubscriptionController

import scala.io.StdIn

/**
  * Created by Denis Gridnev on 11.11.2017.
  */
object Main extends App {
  implicit val system = ActorSystem("subs-storage")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val controllers = Seq(
    new SubscriptionController
  )

  val route = controllers.map(_.route).reduce(_ ~ _)
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8001)

  println(s"Server online at http://localhost:8001/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
