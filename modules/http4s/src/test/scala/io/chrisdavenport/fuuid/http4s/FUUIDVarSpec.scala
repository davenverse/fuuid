package io.chrisdavenport.fuuid.http4s

import io.chrisdavenport.fuuid.{FUUID, FUUIDArbitraries}
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalacheck._
import munit.ScalaCheckSuite
import org.http4s.Uri
import org.scalacheck.Prop.forAll

class FUUIDVarSpec extends ScalaCheckSuite with FUUIDArbitraries {
  property("FUUID Extractor in Path work properly given a valid UUID") {
    forAll { (validFuuid: FUUID) =>
      assertEquals(
        path"/v1" / Uri.Path.Segment(validFuuid.show) match {
          case Root / "v1" / FUUIDVar(uuid @ _) => uuid.eqv(validFuuid)
          case _ => false
        },
        true
      )
    }
  }

  property("FUUID Extractor in Path fail given an invalid UUID") {
    forAll(Gen.alphaStr) { (invalidUuid: String) =>
      assertEquals(
        path"/v1" / Uri.Path.Segment(invalidUuid) match {
          case Root / "v1" / FUUIDVar(uuid @ _) => true
          case _ => false
        },
        false
      )
    }
  }
}
