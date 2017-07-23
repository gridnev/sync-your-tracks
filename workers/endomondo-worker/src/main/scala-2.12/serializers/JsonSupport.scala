package serializers

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, native}

/**
  * Created by Denis Gridnev on 23.07.2017.
  */
trait JsonSupport extends Json4sSupport {
  implicit val serialization = native.Serialization

  implicit def json4sFormats: Formats = DefaultFormats
}
