package model

case class TaskResult(success: Boolean, wCount: Option[Int] = None, errorMessage: Option[String] = None)
case class ActionEvent(subId:String, result: TaskResult)
