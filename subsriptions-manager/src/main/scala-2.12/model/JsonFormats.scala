package model

import spray.json.DefaultJsonProtocol._

object JsonFormats {
  implicit val sourceFormat = jsonFormat1(Source)
  implicit val targetFormat = jsonFormat1(Target)
  implicit val taskFormat = jsonFormat3(Task)
  implicit val subscription = jsonFormat3(Subscription)
}
