package io.chrisdavenport.fuuid.http4s

import cats.effect.IO
import io.chrisdavenport.fuuid.FUUID
import org.http4s.dsl.io._
import org.specs2.mutable.Specification

class FUUIDVarSpec extends Specification {

  "FUUID Extractor in Path" should {

    "work properly given a valid UUID" in {
      val validUuid = FUUID.randomFUUID[IO].unsafeRunSync()

      (Path(s"/v1/${validUuid.toString}") match {
        case Root / "v1" / FUUIDVar(uuid @ _) => uuid.eqv(validUuid)
        case _ => false
      }) must beTrue

    }

    "fail given an invalid UUID" in {

      (Path("/v1/invalidUuid") match {
        case Root / "v1" / FUUIDVar(uuid @ _) => true
        case _ => false
      }) must beFalse

    }
  }
}
