import akka.actor.{ActorSystem, Props}

/**
  * Created by Denis Gridnev on 06.08.2017.
  */
object Main extends App {
  val system = ActorSystem("HelloSystem")

  val senderActor = system.actorOf(Props[SenderActor], name = "sender-actor")
  val receiverActor = system.actorOf(Props(new ActorReceiver(senderActor)), name = "receiver-actor")
}
