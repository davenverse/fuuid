package io.chrisdavenport.fuuid

import cats.kernel.laws.discipline.{HashTests, OrderTests}
import munit.{CatsEffectSuite, DisciplineSuite}
import cats.implicits._

class FUUIDTests extends CatsEffectSuite with DisciplineSuite with FUUIDArbitraries {
  checkAll("FUUID", HashTests[FUUID].hash)
  checkAll("FUUID", OrderTests[FUUID].order)
}
