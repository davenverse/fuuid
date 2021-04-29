package io.chrisdavenport.fuuid

import org.specs2.mutable.Specification
import org.typelevel.discipline.specs2.mutable.Discipline
import cats.kernel.laws.discipline.HashTests
import cats.kernel.laws.discipline.OrderTests
import cats.implicits._

class FUUIDTests extends Specification with Discipline with FUUIDArbitraries {

  checkAll("FUUID", HashTests[FUUID].hash)
  checkAll("FUUID", OrderTests[FUUID].order)
}
