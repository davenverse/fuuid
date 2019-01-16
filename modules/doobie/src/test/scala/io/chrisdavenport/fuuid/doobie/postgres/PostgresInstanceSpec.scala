package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect.{ContextShift, IO}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.specs2._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.testcontainersspecs2.ForAllTestContainer
import com.dimafeng.testcontainers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import org.specs2._
import scala.concurrent.ExecutionContext.Implicits.global

class PostgresInstanceSpec extends mutable.Specification with IOChecker with ForAllTestContainer {
  sequential
  implicit val contextShiftIO: ContextShift[IO] = IO.contextShift(global)

  override lazy val container = GenericContainer(
    "postgres",
    List(5432),
    Map(
      "POSTGRES_DB" -> dbName,
      "POSTGRES_USER" -> dbUserName,
      "POSTGRES_PASSWORD" -> dbPassword,
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


  override def afterStart(): Unit = {
    sql"""
    CREATE TABLE IF NOT EXISTS PostgresInstanceSpec (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  def insertId(fuuid: FUUID): Update0 = {
    sql"""INSERT into PostgresInstanceSpec (id) VALUES ($fuuid)""".update
  }
  val fuuid = FUUID.randomFUUID[IO].unsafeRunSync

  check(sql"SELECT id from PostgresInstanceSpec".query[FUUID])
  check(insertId(fuuid))

}
