package model

import spray.json.DefaultJsonProtocol._

object JsonFormats {
  implicit val sourceFormat = jsonFormat1(Source)
  implicit val targetFormat = jsonFormat1(Target)
  implicit val taskFormat = jsonFormat2(Task)
}
