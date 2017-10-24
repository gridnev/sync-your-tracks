package model

case class Task(source: Source, target: Target)
case class Subscription(source: Source, target: Target)
case class Source(id: String/*, data:ConnectionData*/)
case class Target(id: String/*, data:ConnectionData*/)
