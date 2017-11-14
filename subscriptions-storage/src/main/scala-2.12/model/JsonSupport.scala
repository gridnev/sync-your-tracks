package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport  {
  implicit val sourceFormat = jsonFormat2(Source)
  implicit val targetFormat = jsonFormat2(Target)
  implicit val subscriptionFormat = jsonFormat3(Subscription)
}
