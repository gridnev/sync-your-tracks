package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model._
import services.SubscriptionService

import scala.util.{Failure, Success}

class SubscriptionController(service: SubscriptionService) extends JsonSupport {

  val route =
    pathPrefix("subscriptions") {
      get {
        getSubscriptions()
      } ~
      post {
        decodeRequest {
          entity(as[Seq[Subscription]]) { subs =>
            saveSubscriptions(subs)
          }
        }
      }
    }

  protected def getSubscriptions(): Route = {
    onComplete(service.get) {
      case Success(subs) => complete(subs)
      case Failure(_) => complete(StatusCodes.InternalServerError)
    }
  }

  protected def saveSubscriptions(subs: Seq[Subscription]): Route = {
    onComplete(service.set(subs)) {
      case Success(_) => complete(StatusCodes.OK)
      case Failure(_) => complete(StatusCodes.InternalServerError)
    }
  }
}
