package io.chrisdavenport.fuuid.http4s

import io.chrisdavenport.fuuid.{FUUID, FUUIDArbitraries}
import io.chrisdavenport.fuuid.http4s.implicits._
import org.http4s.dsl.io._
import org.scalacheck._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class FUUIDQueryParamDecoder extends Specification with ScalaCheck with FUUIDArbitraries {

  object IdQueryParamMatcher extends QueryParamDecoderMatcher[FUUID]("id")

  "FUUID QueryParamDecoder" should {

    "work properly given a valid UUID" in prop { validFuuid: FUUID =>
      IdQueryParamMatcher.unapply(Map("id" -> List(validFuuid.show))) must beSome(validFuuid)
    }

    "fail given an invalid UUID" in prop { invalidUuid: String =>
      IdQueryParamMatcher.unapply(Map("id" -> List(invalidUuid))) must beNone
    }.setArbitrary(Arbitrary(Gen.alphaStr))
  }
}
