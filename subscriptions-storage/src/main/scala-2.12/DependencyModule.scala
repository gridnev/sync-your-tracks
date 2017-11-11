package services

import com.softwaremill.macwire.wire
import dao.SubscriptionsDaoImpl

trait DependencyModule {
  lazy val dao = wire[SubscriptionsDaoImpl]
  lazy val service = wire[SubscriptionServiceImpl]
}
