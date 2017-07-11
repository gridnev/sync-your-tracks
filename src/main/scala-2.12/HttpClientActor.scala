import HttpClientActor.{Auth, AuthResult, GetWorkouts}
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import scala.concurrent.Future
class HttpClientActor extends Actor
  with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)
  var authToken:Option[String] = None

  def receive = {
    case Auth(email, password) => http.singleRequest(HttpRequest(
      uri = s"https://api.mobile.endomondo.com/mobile/auth?action=pair&deviceId=27132407-5b55-5863-b150-7925b8d092a2&country=RU&email=$email&password=$password"))
          .map(r=>self ! AuthResult(r))

    case GetWorkouts(maxResults) => http.singleRequest(HttpRequest(
      uri = s"https://api.mobile.endomondo.com/mobile/api/workouts?authToken=$authToken&fields=simple&maxResults=$maxResults"))
      .pipeTo(self)

    case AuthResult(HttpResponse(StatusCodes.OK, headers, entity, _)) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        val regex = "authToken=(\\w+)".r
        val strs = body.utf8String.split("\\r\\n|\\n|\\r") collect {
          case regex(token) => token
        }
        authToken = strs.headOption
        log.info("auth")
      }

    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        log.info("Got response, body: " + body.utf8String)
      }

    case resp @ HttpResponse(code, _, _, _) =>
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
  }

  def makeAuthGet(uri:String)={
    authToken.fold({
      self ! Auth
    })
  }

  def getAuthToken(email:String, password:String): Future[Option[String]] = {

    http.singleRequest(HttpRequest(
      uri = s"https://api.mobile.endomondo.com/mobile/auth?action=pair&deviceId=27132407-5b55-5863-b150-7925b8d092a2&country=RU&email=$email&password=$password"))
      .map {
        case HttpResponse(StatusCodes.OK, headers, entity, _) =>
          entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
          val regex = "authToken=(\\w+)".r
          body.utf8String.split("\\r\\n|\\n|\\r") collect {
            case regex(token) => token
          } headOption
        }
        case _ => None
      }
  }

}

object HttpClientActor{
  case class Auth(email:String, password:String)
  case class GetWorkouts(count:Int)

  case class AuthResult(response:HttpResponse)
}
