package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import com.dimafeng.testcontainers.PostgreSQLContainer
import munit.CatsEffectSuite
import doobie._
import doobie.munit.IOChecker
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._

class PostgresInstanceSpec extends CatsEffectSuite with IOChecker {
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

  test("Select from postgres") {
    check(sql"SELECT id from PostgresInstanceSpec".query[FUUID])
  }
  test("insert uuid") {
    check(insertId(fuuid))
  }
}
