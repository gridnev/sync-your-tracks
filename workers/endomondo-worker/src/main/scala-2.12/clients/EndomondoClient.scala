package clients

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.Uri
import model.Workout

import scala.concurrent.Future

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
class EndomondoClient(host: String, port: Int)(implicit system: ActorSystem) extends BaseClient(host, port) {
  def getWorkouts(): Future[Seq[Workout]] = {
    sendAndReceiveAs[Seq[Workout]](Get(s"/endomondo/workouts"))
  }

  def postWorkouts(workouts: String): Future[Unit] = {
    sendAndReceive(Post(Uri("/endomondo/workouts"), Some(workouts)), _ => Future.unit)
  }
}


