package io.chrisdavenport.fuuid

import java.util.UUID
import cats.effect.IO
import org.specs2._

object FUUIDSpec extends mutable.Specification with ScalaCheck {

  "FUUID.fromString" should {
    "Fail when parsing an invalid string" in {
      FUUID.fromString("What up yo!")
        .isLeft must_=== true
    }
    "Fail when parsing invalid uuid" in {
      FUUID.fromString("2630147c-4a18-4866-9bbd-b2d89acef83z").isLeft must_=== true
    }
    "Succeed when parsing a valid UUID" in {
      FUUID.randomFUUID[IO]
        .map(_.toString)
        .map(FUUID.fromString)
        .map(_.isRight)
        .unsafeRunSync must_=== true
    }
  }

  "FUUID.nameBased" should {
    "produce a valid UUID" in {
      (for {
        namespace <- FUUID.randomFUUID[IO]
        namebased <- FUUID.nameBased[IO](namespace, "What up yo!")
        parsed <- FUUID.fromStringF[IO](namebased.toString)
      } yield parsed)
        .attempt
        .unsafeRunSync
        .isRight must_=== true
    }

    "conform to an example" in {
      val namespace = FUUID.fromStringF[IO]("dc79a6bc-de3c-5bc7-a877-712bea708d8f").unsafeRunSync()
      val name = "What up yo!"

      val expected = FUUID.fromStringF[IO]("1cce4593-d217-5b33-be0d-2e81462e79d3").unsafeRunSync()

      FUUID.nameBased[IO](namespace, name).unsafeRunSync must_=== expected
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

  // FUUID.fuuid("kasdfasd")



}

