package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.specs2._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import org.specs2._
import org.specs2.specification.BeforeAll

class PostgresInstanceSpec extends mutable.Specification with IOChecker with BeforeAll {
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres", ""
  )

  def beforeAll(): Unit = {
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