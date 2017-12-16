package model

import spray.json.DefaultJsonProtocol._

object JsonFormats {
  implicit val taskResultFormat = jsonFormat3(TaskResult)
  implicit val ActionEventFormat = jsonFormat2(ActionEvent)
}
