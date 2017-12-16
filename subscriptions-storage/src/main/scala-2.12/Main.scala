import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

/**
  * Created by Denis Gridnev on 11.11.2017.
  */
object Main extends App with DependencyModule {
  implicit val system = ActorSystem("subs-storage")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val controllers = Seq(
    subscriptionController
  )

  val route = controllers.map(_.route).reduce(_ ~ _)
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8002)

  println(s"Server online at http://localhost:8002/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
