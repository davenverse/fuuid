package io.chrisdavenport.fuuid

import org.scalacheck._

trait FUUIDArbitraries {

  implicit val arbFUUID: Arbitrary[FUUID] = Arbitrary(
    Gen.uuid.map(FUUID.fromUUID)
  )

  implicit val arbFUUIDFunction: Arbitrary[FUUID => FUUID] = Arbitrary(
    Arbitrary.arbitrary[FUUID].map(f1 => (_: FUUID) => f1)
  )
}

object FUUIDArbitraries extends FUUIDArbitraries
