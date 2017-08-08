import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
object Main extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    (pathPrefix("workouts") & get) {
      complete(HttpEntity(ContentTypes.`application/json`, "[\n  {\n    \"id\": 8529483,\n    \"resource_state\": 2,\n    \"external_id\": \"2013-08-23-17-04-12.fit\",\n    \"upload_id\": 84130503,\n    \"athlete\": {\n      \"id\": 227615,\n      \"resource_state\": 1\n    },\n    \"name\": \"Afternoon Ride\",\n    \"distance\": 32486.1,\n    \"moving_time\": 5241,\n    \"elapsed_time\": 5427,\n    \"total_elevation_gain\": 566.0,\n    \"type\": \"Ride\",\n    \"start_date\": \"2013-08-24T00:04:12Z\",\n    \"start_date_local\": \"2013-08-23T17:04:12Z\",\n    \"timezone\": \"(GMT-08:00) America/Los_Angeles\",\n    \"start_latlng\": [\n      37.793551,\n      -122.2686\n    ],\n    \"end_latlng\": [\n      37.792836,\n      -122.268287\n    ],\n    \"achievement_count\": 8,\n    \"pr_count\": 3,\n    \"kudos_count\": 0,\n    \"comment_count\": 0,\n    \"athlete_count\": 1,\n    \"photo_count\": 0,\n    \"total_photo_count\": 0,\n    \"map\": {\n      \"id\": \"a77175935\",\n      \"summary_polyline\": \"cetewLja@zYcG\",\n      \"resource_state\": 2\n    },\n    \"trainer\": false,\n    \"commute\": false,\n    \"manual\": false,\n    \"private\": false,\n    \"flagged\": false,\n    \"average_speed\": 3.4,\n    \"max_speed\": 4.514,\n    \"average_watts\": 163.6,\n    \"max_watts\": 754,\n    \"weighted_average_watts\": 200,\n    \"kilojoules\": 857.6,\n    \"device_watts\": true,\n    \"has_heartrate\": true,\n    \"average_heartrate\": 138.8,\n    \"max_heartrate\": 179.0\n  }\n]"))
    } ~
    (pathPrefix("subscriptions") & get) {
      complete(HttpEntity(ContentTypes.`application/json`, "[{\"source\":{\"id\":\"strava\"},\"target\":{\"id\":\"endomondo\"}}]"))
    } ~
    (pathPrefix("endomondo") & pathPrefix("workouts") & post) {
      decodeRequest {
        entity(as[Any]){
          a =>
            complete{
              println(a)
              "Ok"
            }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8001)

  println(s"Server online at http://localhost:8001/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate())
}
