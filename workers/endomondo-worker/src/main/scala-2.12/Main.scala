import clients.EndomondoClient
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Denis Gridnev on 23.07.2017.
  */
object Main extends App {
  val host = "api.mobile.endomondo.com"
  val email = "denis.gridnev@gmail.com"
  val password = "e1n2d3o4"
  val client = new EndomondoClient(host, email, password)

  for {
    workouts <- client.getWorkouts()
    _ = workouts.foreach(println)
  } yield ()
}
