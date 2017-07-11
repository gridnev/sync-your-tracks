import HttpClientActor.{Auth, GetWorkouts}
import akka.actor.{ActorSystem, Props}

/**
  * Created by Denis Gridnev on 24.06.2017.
  */
object Main extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val httpClient = system.actorOf(Props[HttpClientActor], name = "http-client")
  httpClient ! Auth("denis.gridnev@gmail.com", "e1n2d3o4")

  scala.io.StdIn.readLine("What's your name? ")

  httpClient ! GetWorkouts(10)
}
