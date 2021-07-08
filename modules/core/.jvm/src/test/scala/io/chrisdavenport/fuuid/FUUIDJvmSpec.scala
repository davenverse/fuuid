package io.chrisdavenport.fuuid

import cats.effect.IO
import munit.CatsEffectSuite

class FUUIDJvmSpec extends CatsEffectSuite {
  test("FUUID.nameBased") {
    test("produce a valid UUID") {
      (for {
        namespace <- FUUID.randomFUUID[IO]
        namebased <- FUUID.nameBased[IO](namespace, "What up yo!")
        parsed <- FUUID.fromStringF[IO](namebased.toString)
      } yield parsed).attempt.map(_.isRight).assertEquals(true)
    }

    test("conform to an example") {
      val namespace = FUUID.fromStringF[IO]("dc79a6bc-de3c-5bc7-a877-712bea708d8f").unsafeRunSync()
      val name = "What up yo!"

      val expected = FUUID.fromStringF[IO]("1cce4593-d217-5b33-be0d-2e81462e79d3").unsafeRunSync()

      FUUID.nameBased[IO](namespace, name).assertEquals(expected)
    }
  }
}
