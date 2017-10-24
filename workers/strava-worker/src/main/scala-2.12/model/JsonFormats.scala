package model

import spray.json.DefaultJsonProtocol._

object JsonFormats {
  implicit val sourceFormat = jsonFormat1(Source)
  implicit val targetFormat = jsonFormat1(Target)
  implicit val workoutFormat = jsonFormat3(Workout)
  implicit val taskFormat = jsonFormat3(Task)
}
