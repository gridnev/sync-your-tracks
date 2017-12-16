package model

case class Task(id: String, source: Source, target: Target)
case class Subscription(userId: String, source: Source, target: Target)
case class Source(id: String/*, data:ConnectionData*/)
case class Target(id: String/*, data:ConnectionData*/)
