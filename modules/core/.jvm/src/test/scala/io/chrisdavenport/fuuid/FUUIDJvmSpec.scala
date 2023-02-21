package io.chrisdavenport.fuuid

import cats.ApplicativeThrow
import cats.effect.IO
import munit.CatsEffectSuite

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.reflect.ClassTag

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

  test("FUUID.fuuid survives Java serialization") {
    def serialize(fuuid: FUUID): IO[Array[Byte]] = {
      ApplicativeThrow[IO].catchNonFatal {
        val byteStream = new ByteArrayOutputStream()
        val objectStream = new ObjectOutputStream(byteStream)
        objectStream.writeObject(fuuid)
        objectStream.close()
        byteStream.toByteArray
      }
    }

    def deserialize(bytes: Array[Byte]): IO[FUUID] = ApplicativeThrow[IO].catchNonFatal {
      val byteStream = new ByteArrayInputStream(bytes)
      val objectStream = new ObjectInputStream(byteStream)
      val obj = objectStream.readObject()
      val clazz = implicitly[ClassTag[FUUID]]
      obj match {
        case clazz(fuuid) => fuuid
        case _ => fail("deserialized value not recognized as FUUID", clues(obj))
      }
    }

    FUUID.randomFUUID[IO].flatMap { original =>
      serialize(original).flatMap(deserialize).assertEquals(original)
    }
  }
}
