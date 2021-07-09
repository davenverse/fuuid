package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import munit.{CatsEffectSuite, ScalaCheckSuite}
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie._
import doobie.munit.IOChecker
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid._
import io.chrisdavenport.fuuid.doobie.implicits._
import org.scalacheck.Prop.forAll

class PostgresTraversalSpec
    extends CatsEffectSuite
    with IOChecker
    with ScalaCheckSuite
    with FUUIDArbitraries {

  lazy val container = PostgreSQLContainer()

  lazy val transactor = Transactor.fromDriverManager[IO](
    container.driverClassName,
    container.jdbcUrl,
    container.username,
    container.password
  )

  override def beforeAll(): Unit = {
    container.container.start()

    sql"""
    CREATE TABLE IF NOT EXISTS PostgresTraversalSpec (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  override def afterAll(): Unit = container.container.stop()

  def queryBy(fuuid: FUUID): Query0[FUUID] =
    sql"""SELECT id from PostgresTraversalSpec where id = $fuuid""".query[FUUID]

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into PostgresTraversalSpec (id) VALUES ($fuuid)""".update

  property("Doobie Postgres Meta traverse input and then extraction") {
    forAll { (fuuid: FUUID) =>
      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      assertEquals(action.unsafeRunSync(), fuuid)
    }
  }
  property("Doobie Postgres Meta fail on a non-present value") {
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
