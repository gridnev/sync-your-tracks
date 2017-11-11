package model

/**
  * Created by Denis Gridnev on 11.11.2017.
  */

case class Source(id: String, authData: String)

case class Target(id: String, authData: String)

case class Subscription(userId: String, source: Source, target: Target)