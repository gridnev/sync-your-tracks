import Client.{Connected, PushMessage}
import ClientHandler.Join
import akka.actor.{Actor, ActorRef}

object Client{
  case class Connected(ref: ActorRef)
  case class PushMessage(text: String)
}

class Client(clientHandler: ActorRef, id: String) extends Actor {
  override def receive = {
    case Connected(ref) =>
      context.become(connected(ref))
  }

  def connected(ref: ActorRef): Receive = {
    clientHandler ! Join(id)

    {
      case pm@PushMessage(_) => ref ! pm
    }
  }
}
