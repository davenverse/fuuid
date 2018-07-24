package io.chrisdavenport.fuuid

import cats.tests.CatsSuite
import cats.kernel.laws.discipline.HashTests
import cats.kernel.laws.discipline.OrderTests

class FUUIDTests extends CatsSuite with FUUIDArbitraries {

  checkAll("FUUID", HashTests[FUUID].hash)
  checkAll("FUUID", OrderTests[FUUID].order)
}