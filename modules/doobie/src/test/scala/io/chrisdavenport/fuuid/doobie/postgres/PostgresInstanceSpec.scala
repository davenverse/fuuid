package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.specs2._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.concurrent.ExecutionContext.Implicits.global

class PostgresInstanceSpec extends Specification with IOChecker with BeforeAfterAll {
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
    CREATE TABLE IF NOT EXISTS PostgresInstanceSpec (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  override def afterAll(): Unit = container.container.stop()

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into PostgresInstanceSpec (id) VALUES ($fuuid)""".update
  val fuuid = FUUID.randomFUUID[IO].unsafeRunSync()

  check(sql"SELECT id from PostgresInstanceSpec".query[FUUID])
  check(insertId(fuuid))

}
