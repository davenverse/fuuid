package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect.{ContextShift, IO}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.fuuid._
import io.chrisdavenport.testcontainersspecs2.ForAllTestContainer
import com.dimafeng.testcontainers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import org.specs2._
import scala.concurrent.ExecutionContext.Implicits.global

class PostgresTraversalSpec extends mutable.Specification
  with ScalaCheck with FUUIDArbitraries with ForAllTestContainer {
  sequential
  implicit val contextShiftIO: ContextShift[IO] = IO.contextShift(global)

  override lazy val container = GenericContainer(
    "postgres",
    List(5432),
    Map(
      "POSTGRES_DB" -> dbName,
      "POSTGRES_USER" -> dbUserName,
      "POSTGRES_PASSWORD" -> dbPassword
    ),
    waitStrategy = new LogMessageWaitStrategy()
      .withRegEx(".*database system is ready to accept connections.*\\s")
      .withTimes(2)
      .withStartupTimeout(Duration.of(60, SECONDS))
  )

  lazy val driverName = "org.postgresql.Driver"
  lazy val jdbcUrl = s"jdbc:postgresql://${container.container.getContainerIpAddress()}:${container.container.getMappedPort(5432)}/${dbName}"
  lazy val dbUserName = "user"
  lazy val dbPassword = "password"
  lazy val dbName = "db"

  lazy val transactor = Transactor.fromDriverManager[IO](
    driverName,
    jdbcUrl,
    dbUserName,
    dbPassword
  )

  // lazy val transactor = Transactor.fromDriverManager[IO](
  //   "org.postgresql.Driver",
  //   "jdbc:postgresql:world",
  //   "postgres", ""
  // )

  override def afterStart(): Unit = {
    sql"""
    CREATE TABLE IF NOT EXISTS PostgresTraversalSpec (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  def queryBy(fuuid: FUUID): Query0[FUUID] = {
    sql"""SELECT id from PostgresTraversalSpec where id = ${fuuid}""".query[FUUID]
  }

  def insertId(fuuid: FUUID): Update0 = {
    sql"""INSERT into PostgresTraversalSpec (id) VALUES ($fuuid)""".update
  }

  "Doobie Postgres Meta" should {
    "traverse input and then extraction" in prop { fuuid: FUUID =>

      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      action.unsafeRunSync() must_=== fuuid
    }
    "fail on a non-present value" in prop { fuuid: FUUID =>
      queryBy(fuuid)
        .unique
        .transact(transactor)
        .attempt
        .map(_.isLeft)
        .unsafeRunSync() must_=== true
    }
  }

}
