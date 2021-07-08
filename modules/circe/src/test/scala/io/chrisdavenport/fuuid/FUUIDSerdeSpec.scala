package io.chrisdavenport.fuuid

import io.chrisdavenport.fuuid.circe._
import io.circe.{KeyDecoder, KeyEncoder}
import io.circe.syntax._
import munit.{CatsEffectSuite, ScalaCheckSuite}
import org.scalacheck.Prop.forAll

class FUUIDSerdeSpec extends CatsEffectSuite with ScalaCheckSuite with FUUIDArbitraries {
  property("circe serialization and deserialization correct serialize and deserialize") {
    forAll { (validFUUID: FUUID) =>
      assertEquals(validFUUID.asJson.as[FUUID], Right(validFUUID))
    }
  }

  property("circe serialization and deserialization correct key serialize and deserialize") {
    forAll { (validFUUID: FUUID) =>
      assertEquals(KeyDecoder[FUUID].apply(KeyEncoder[FUUID].apply(validFUUID)), Some(validFUUID))
    }
  }
}
