package io.chrisdavenport.fuuid.http4s

import cats.implicits.catsSyntaxOptionId
import io.chrisdavenport.fuuid.http4s.implicits._
import io.chrisdavenport.fuuid.{FUUID, FUUIDArbitraries}
import munit.ScalaCheckSuite
import org.http4s.dsl.io._
import org.scalacheck.Gen
import org.scalacheck.Prop._

class FUUIDQueryParamDecoder extends ScalaCheckSuite with FUUIDArbitraries {
  object IdQueryParamMatcher extends QueryParamDecoderMatcher[FUUID]("id")

  property("FUUID QueryParamDecoder work properly given a valid UUID") {
    forAll { (validFuuid: FUUID) =>
      assertEquals(IdQueryParamMatcher.unapply(Map("id" -> List(validFuuid.show))), validFuuid.some)
    }
  }

  property("FUUID QueryParamDecoder fail given an invalid UUID") {
    forAll(Gen.alphaStr) { (invalidUuid: String) =>
      assertEquals(IdQueryParamMatcher.unapply(Map("id" -> List(invalidUuid))), None)
    }
  }
}
