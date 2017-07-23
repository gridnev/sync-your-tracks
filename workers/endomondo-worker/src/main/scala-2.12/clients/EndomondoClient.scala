package clients

import scala.concurrent.Future
import akka.http.scaladsl.client.RequestBuilding.{Get}

/**
  * Created by Denis Gridnev on 23.07.2017.
  */
class EndomondoClient(host: String) extends BaseClient(host) {
  def getWorkouts(): Future[Seq[Workout]] = {
    val authToken = "ZoYSf1ySSSeW01N0YjJKHw"
    sendAndReceiveAs[Response[Seq[Workout]]](Get(s"/mobile/api/workouts?authToken=$authToken&fields=simple&maxResults=10"))
      .map(_.data)
  }
}

case class Response[T](data: T)
case class Workout(distance: String, duration: String, id: String)
