package services

import dao.{SubscriptionsDao, SubscriptionsDaoImpl}
import com.softwaremill.macwire.wire
import model.Subscription

import scala.concurrent.Future

/**
  * Created by Denis Gridnev on 11.11.2017.
  */
trait SubscriptionService {
  def set(subs: Seq[Subscription]): Future[Unit]

  def get(): Future[Seq[Subscription]]
}

class SubscriptionServiceImpl(dao: SubscriptionsDao) extends SubscriptionService {
  override def set(subs: Seq[Subscription]): Future[Unit] = {
    dao.set(subs)
  }

  override def get(): Future[Seq[Subscription]] = {
    dao.get()
  }
}

