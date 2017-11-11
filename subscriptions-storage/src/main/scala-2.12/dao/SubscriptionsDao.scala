package dao

import org.mongodb.scala._

/**
  * Created by Denis Gridnev on 11.11.2017.
  */
trait SubscriptionsDao {
  def get()
  def set()
}

class SubscriptionsDaoImpl extends SubscriptionsDao{
  // To directly connect to the default server localhost on port 27017
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("subscriptions")

  override def get(): Unit = {
    database.getCollection[]()
  }

  override def set(): Unit = {}
}
