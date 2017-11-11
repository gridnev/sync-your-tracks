package services

import dao.{SubscriptionsDao, SubscriptionsDaoImpl}
import com.softwaremill.macwire.wire

/**
  * Created by Denis Gridnev on 11.11.2017.
  */
trait SubscriptionService{
  def set()
  def get()
}

class SubscriptionServiceImpl(dao: SubscriptionsDao) extends SubscriptionService {
  override def set(): Unit = {
    dao.set()
  }

  override def get(): Unit = {
    dao.get()
  }
}

trait DependencyModule {
  lazy val dao = wire[SubscriptionsDaoImpl]
  lazy val service = wire[SubscriptionServiceImpl]
}