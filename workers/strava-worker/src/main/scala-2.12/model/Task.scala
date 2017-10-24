package model


case class Task(source: Source, target: Target, workouts: Option[Seq[Workout]] = None)
case class Source(id: String/*, data:ConnectionData*/)
case class ConnectionData()

case class Target(id: String/*, data:String*/)

case class Workout(distance: String, elapsed_time: String, id: String)
