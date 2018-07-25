package io.chrisdavenport.fuuid

import java.util.UUID

import io.chrisdavenport.fuuid.circe._
import io.circe.syntax._
import org.scalatest.EitherValues._
import org.scalatest.{FlatSpec, Matchers}

class FUUIDSerdeSpec extends FlatSpec with Matchers {
  val uuid = UUID.fromString("0679edff-ed3e-469d-b835-feb06f39b553")
  val fuuid = FUUID.fromUUID(uuid)

  it should "successfully serialize and deserialize" in {
    fuuid.asJson.as[FUUID].right.value shouldEqual fuuid
  }
}
