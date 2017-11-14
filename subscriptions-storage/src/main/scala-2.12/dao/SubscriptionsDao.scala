package dao

import model.{Source, Subscription, Target}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Denis Gridnev on 11.11.2017.
  */
trait SubscriptionsDao {
  def get(): Future[Seq[Subscription]]
  def set(subs: Seq[Subscription]): Future[Unit]
}

class SubscriptionsDaoImpl extends SubscriptionsDao{
  // To directly connect to the default server localhost on port 27017

  val codecRegistry = fromRegistries(fromProviders(classOf[Source], classOf[Target], classOf[Subscription]), DEFAULT_CODEC_REGISTRY )

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("main1")
    .withCodecRegistry(codecRegistry)

  override def get(): Future[Seq[Subscription]] = {
    database.getCollection[Subscription]("subscriptions").find().toFuture().recover{
      case er => {
        println(er.getMessage)
        Seq.empty
      }
    }
  }

  override def set(subs: Seq[Subscription]): Future[Unit] = {
    database.getCollection[Subscription]("subscriptions").insertMany(subs).toFuture().map(r=>{
      println(r)
    }).recover{
      case er => println(er.getMessage)
    }
  }
}
