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

}

