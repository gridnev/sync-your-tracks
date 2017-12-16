package client

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.Uri
import model.Workout

import scala.concurrent.Future

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
class StravaClient(host: String, port: Int)(implicit system: ActorSystem) extends BaseClient(host, port) {
  def getWorkouts(): Future[Seq[Workout]] = {
    sendAndReceiveAs[Seq[Workout]](Get(s"/strava/workouts"))
  }

  def postWorkouts(workouts: String): Future[Unit] = {
    sendAndReceive(Post(Uri("/strava/workouts"), Some(workouts)), _ => Future.unit)
  }
}


