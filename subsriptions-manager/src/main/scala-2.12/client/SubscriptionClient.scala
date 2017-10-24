package client

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding._
import model.Subscription

import scala.concurrent.Future

/**
  * Created by Denis Gridnev on 29.07.2017.
  */
class SubscriptionClient(host: String, port: Int)(implicit system: ActorSystem) extends BaseClient(host, port) {
  def getSubscriptions(): Future[Seq[Subscription]] = {
    sendAndReceiveAs[Seq[Subscription]](Get(s"/subscriptions"))
  }
}

