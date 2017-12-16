import Client.PushMessage
import ClientHandler.{Join, Notify}
import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

object ClientHandler{
  case class Join(id: String)
  case class Notify(id: String, message: String)
}

class ClientHandler extends Actor {
  val clients = new mutable.HashMap[String, ActorRef]()

  override def receive = {
    case Join(id) =>
      clients.update(id, sender())
    case Notify(id, message) =>
      clients.get(id).foreach(_ ! PushMessage(message))
  }
}
