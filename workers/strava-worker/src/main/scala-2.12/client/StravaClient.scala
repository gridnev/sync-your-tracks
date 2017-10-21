package client

import akka.http.scaladsl.client.RequestBuilding._
import scala.concurrent.Future

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
class StravaClient(host: String, port: Int) extends BaseClient(host, port) {
  def getWorkouts(): Future[Seq[Workout]] = {
    sendAndReceiveAs[Seq[Workout]](Get(s"/workouts"))
  }

  def postWorkouts(workouts: String): Future[Unit] = {
    sendAndReceive(Post(uri="/strava/workouts", Some(workouts)), _ => Future.unit)
  }
}

case class Workout(distance: String, elapsed_time: String, id: String)
