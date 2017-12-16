package model

import java.time.LocalDateTime


case class Task(id: String, source: Source, target: Target, workouts: Option[Seq[Workout]] = None)
case class Source(id: String/*, data:ConnectionData*/)
case class ConnectionData()

case class Target(id: String/*, data:String*/)

case class Workout(distance: String, elapsed_time: String, id: String)

case class TaskResult(success: Boolean, wCount: Option[Int] = None, errorMessage: Option[String] = None)
case class ActionEvent(subId:String, result: TaskResult)