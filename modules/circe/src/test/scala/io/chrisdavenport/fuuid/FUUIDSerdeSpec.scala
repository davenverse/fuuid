package io.chrisdavenport.fuuid

import io.chrisdavenport.fuuid.circe._
import io.circe.syntax._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class FUUIDSerdeSpec extends Specification with ScalaCheck with FUUIDArbitraries {
  "circe serialization and deserialization" should {
    "correct serialize and deserialize" in prop { validFUUID: FUUID =>
      validFUUID.asJson.as[FUUID] must beRight.like{
        case fuuid => fuuid === validFUUID
      }
    }
  }
}
