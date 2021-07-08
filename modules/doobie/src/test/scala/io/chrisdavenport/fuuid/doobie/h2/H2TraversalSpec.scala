package io.chrisdavenport.fuuid.doobie.h2

import cats.effect.IO
import munit.{CatsEffectSuite, ScalaCheckSuite}
import doobie._
import doobie.h2.implicits._
import doobie.munit.IOChecker
import doobie.implicits._
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.fuuid._
import org.scalacheck.Prop.forAll

class H2TraversalSpec
    extends CatsEffectSuite
    with IOChecker
    with ScalaCheckSuite
    with FUUIDArbitraries {

  lazy val transactor: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "org.h2.Driver",
      url = "jdbc:h2:mem:testH2Table;DB_CLOSE_DELAY=-1",
      user = "sa",
      pass = ""
    )

  override def beforeAll(): Unit = {
    sql"""
    CREATE TABLE testH2Table (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  def queryBy(fuuid: FUUID): Query0[FUUID] =
    sql"""SELECT id from testH2Table where id = $fuuid""".query[FUUID]

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into testH2Table (id) VALUES ($fuuid)""".update
  property("Doobie H2 Meta traverse input and then extraction") {

    forAll { (fuuid: FUUID) =>
      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      assertEquals(action.unsafeRunSync(), fuuid)
    }
  }
  property("Doobie H2 Meta fail on a non-present value") {
    forAll { (fuuid: FUUID) =>
      assertEquals(
        queryBy(fuuid).unique
          .transact(transactor)
          .attempt
          .map(_.isLeft)
          .unsafeRunSync(),
        true
      )
    }
  }
}
