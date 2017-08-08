package clients

import scala.concurrent.Future
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.util.ByteString
/**
  * Created by Denis Gridnev on 23.07.2017.
  */
class EndomondoClient(host: String, port: Int, email: String, password: String) extends BaseClient(host, port) {
  def getWorkouts(): Future[Seq[Workout]] = {
    for {
      authToken <- getAuthToken()
      result <- sendAndReceiveAs[Response[Seq[Workout]]](Get(s"/mobile/api/workouts?authToken=$authToken&fields=simple&maxResults=10"))
    } yield result.data
  }

  def postWorkouts(workouts: String): Future[Unit] = {
    sendAndReceive(Post(uri="/endomondo/workouts", Some(workouts)), _ => Future.unit)
  }

  private def getAuthToken(): Future[String] = {
    sendAndReceive(
      Get(s"/mobile/auth?action=pair&deviceId=27132407-5b55-5863-b150-7925b8d092a2&country=RU&email=$email&password=$password"),
      s => s.entity.dataBytes.runFold(ByteString(""))(_ ++ _)) map { byteString =>
      val regex = "authToken=(\\w+)".r
      byteString.utf8String.split("\\r\\n|\\n|\\r") collectFirst {
        case regex(token) => token
      } getOrElse {
        println(s"Auth not completed. Resp: ${byteString.utf8String}")
        throw new Error
      }
    }
  }
}

case class Response[T](data: T)
case class Workout(distance: String, duration: String, id: String)
