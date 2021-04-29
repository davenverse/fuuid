package io.chrisdavenport.fuuid.http4s

import io.chrisdavenport.fuuid.{FUUID, FUUIDArbitraries}
import org.http4s.dsl.io._
import org.scalacheck._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class FUUIDVarSpec extends Specification with ScalaCheck with FUUIDArbitraries {

  "FUUID Extractor in Path" should {

    "work properly given a valid UUID" in prop { validFuuid: FUUID =>
      (Path(s"/v1/${validFuuid.show}") match {
        case Root / "v1" / FUUIDVar(uuid @ _) => uuid.eqv(validFuuid)
        case _ => false
      }) must beTrue

    }

    "fail given an invalid UUID" in prop { invalidUuid: String =>
      (Path(s"/v1/$invalidUuid") match {
        case Root / "v1" / FUUIDVar(uuid @ _) => true
        case _ => false
      }) must beFalse

    }.setArbitrary(Arbitrary(Gen.alphaStr))
  }
}
