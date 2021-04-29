package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid._
import io.chrisdavenport.fuuid.doobie.implicits._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.concurrent.ExecutionContext.Implicits.global

class PostgresTraversalSpec
    extends Specification
    with ScalaCheck
    with FUUIDArbitraries
    with BeforeAfterAll {
  sequential
  implicit val contextShiftIO: ContextShift[IO] = IO.contextShift(global)

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
    sql"""SELECT id from PostgresTraversalSpec where id = ${fuuid}""".query[FUUID]

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into PostgresTraversalSpec (id) VALUES ($fuuid)""".update

  "Doobie Postgres Meta" should {
    "traverse input and then extraction" in prop { fuuid: FUUID =>
      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      action.unsafeRunSync() must_=== fuuid
    }
    "fail on a non-present value" in prop { fuuid: FUUID =>
      queryBy(fuuid).unique
        .transact(transactor)
        .attempt
        .map(_.isLeft)
        .unsafeRunSync() must_=== true
    }
  }

}
