import clients.EndomondoClient
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Denis Gridnev on 23.07.2017.
  */
object Main extends App {
  val host = "api.mobile.endomondo.com"
  val client = new EndomondoClient(host)

  for {
    workouts <- client.getWorkouts()
    _ = workouts.foreach(println)
  } yield ()
}
