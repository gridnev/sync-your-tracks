package model

import spray.json.DefaultJsonProtocol._

object JsonFormats {
  implicit val sourceFormat = jsonFormat1(Source)
  implicit val targetFormat = jsonFormat1(Target)
  implicit val workoutFormat = jsonFormat3(Workout)
  implicit val taskFormat = jsonFormat4(Task)
  implicit val taskResultFormat = jsonFormat3(TaskResult)
  implicit val ActionEventFormat = jsonFormat2(ActionEvent)
}
