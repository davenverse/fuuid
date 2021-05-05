package io.chrisdavenport.fuuid

import cats.effect.IO
import cats.effect.testing.specs2.CatsEffect
import org.specs2.mutable.Specification

class FUUIDJvmSpec extends Specification with CatsEffect {

  import cats.effect.unsafe.implicits.global

  "FUUID.nameBased" should {
    "produce a valid UUID" in {
      (for {
        namespace <- FUUID.randomFUUID[IO]
        namebased <- FUUID.nameBased[IO](namespace, "What up yo!")
        parsed <- FUUID.fromStringF[IO](namebased.toString)
      } yield parsed).attempt.map(_.isRight must_=== true)
    }

    "conform to an example" in {
      val namespace = FUUID.fromStringF[IO]("dc79a6bc-de3c-5bc7-a877-712bea708d8f").unsafeRunSync()
      val name = "What up yo!"

      val expected = FUUID.fromStringF[IO]("1cce4593-d217-5b33-be0d-2e81462e79d3").unsafeRunSync()

      FUUID.nameBased[IO](namespace, name).map(_ must_=== expected)
    }
  }
}
