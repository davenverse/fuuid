package io.chrisdavenport.fuuid

import cats.effect.IO
import org.specs2._

object FUUIDSpec extends mutable.Specification with ScalaCheck {

  "FUUID.fromString" should {
    "Fail when parsing an invalid string" in {
      FUUID.fromString("What up yo!")
        .isLeft must_=== true
    }
    "Succeed when parsing a valid UUID" in {
      FUUID.randomFUUID[IO]
        .map(_.toString)
        .map(FUUID.fromString)
        .map(_.isRight)
        .unsafeRunSync must_=== true
    }
  }

  "FUUID.hashCode" should {
    "have same hashcode as uuid" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = java.util.UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.hashCode must_=== uuid.hashCode
    }
  }

  "FUUID.equals" should {
    "be equal for the same FUUID" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = java.util.UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.equals(fuuid) must_=== true
    }
  }

  "FUUID.eqv" should {
    "be equal for the same FUUID" in {
      val baseString = "00000000-075b-cd15-0000-0000075bcd15"
      // Easy in for testing
      val uuid = java.util.UUID.fromString(baseString)
      val fuuid = FUUID.fromUUID(uuid)
      fuuid.eqv(fuuid) must_=== true
    }
  }

}

