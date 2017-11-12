import com.softwaremill.macwire.wire
import controllers.SubscriptionController
import dao.SubscriptionsDaoImpl
import services.SubscriptionServiceImpl

trait DependencyModule {
  lazy val subscriptionDao = wire[SubscriptionsDaoImpl]
  lazy val subscriptionService = wire[SubscriptionServiceImpl]
  lazy val subscriptionController = wire[SubscriptionController]
}
