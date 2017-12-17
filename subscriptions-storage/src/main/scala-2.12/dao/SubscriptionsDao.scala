package dao

import com.mongodb.MongoCredential
import model.{Source, Subscription, Target}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoDatabase, ServerAddress}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import com.mongodb.connection.{ClusterSettings, SslSettings}
import org.mongodb.scala.MongoCredential._
import org.mongodb.scala.connection.NettyStreamFactoryFactory

import scala.collection.JavaConverters._
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
  val uri = "mongodb://admin:admin@cluster0-shard-00-00-hkeoa.mongodb.net:27017,cluster0-shard-00-01-hkeoa.mongodb.net:27017,cluster0-shard-00-02-hkeoa.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin"

 /* val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(List(
    new ServerAddress("cluster0-shard-00-00-hkeoa.mongodb.net", 27017),
    new ServerAddress("cluster0-shard-00-01-hkeoa.mongodb.net", 27017),
    new ServerAddress("cluster0-shard-00-02-hkeoa.mongodb.net", 27017)
  ).asJava).build()

  val settings: MongoClientSettings = MongoClientSettings.builder()
    .credentialList(List(createCredential("admin", "main1", "admin".toCharArray)).asJava)
    .clusterSettings(clusterSettings)
    .sslSettings(SslSettings.builder()
      .enabled(true)
      .build())
    .streamFactoryFactory(NettyStreamFactoryFactory())
    .build()*/

  System.setProperty("org.mongodb.async.type", "netty")

  val mongoClient: MongoClient = MongoClient(uri)
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
