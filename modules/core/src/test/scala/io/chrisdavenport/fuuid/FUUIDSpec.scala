package io.chrisdavenport.fuuid

import java.util.UUID

import cats.effect.IO
import org.specs2.mutable.Specification
import cats.effect.testing.specs2.CatsEffect

class FUUIDSpec extends Specification with CatsEffect {

  "FUUID.fromString" should {
    "Fail when parsing an invalid string" in {
      FUUID.fromString("What up yo!").isLeft must_=== true
    }
    "Fail when parsing invalid uuid" in {
      FUUID.fromString("2630147c-4a18-4866-9bbd-b2d89acef83z").isLeft must_=== true
    }
    "Succeed when parsing a valid UUID" in {
      FUUID
        .randomFUUID[IO]
        .map(_.toString)
        .map(FUUID.fromString)
        .map(_.isRight must_=== true)
    }
  }

  "FUUID.hashCode" should {
    "have same hashcode as uuid" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.hashCode must_=== uuid.hashCode
    }
  }

  "FUUID.equals" should {
    "be equal for the same FUUID" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.equals(fuuid) must_=== true
    }
  }

  "FUUID.eqv" should {
    "be equal for the same FUUID" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.eqv(fuuid) must_=== true
    }
  }

  "FUUID.fuuid" should {
    "compile for a literal" in {
      FUUID.fuuid("00000000-075b-cd15-0000-0000075bcd15")
      ok
    }
  }
}
