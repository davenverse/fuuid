package io.chrisdavenport.fuuid.doobie.postgres

import org.specs2._
import org.specs2.specification.BeforeAll
import doobie._
import doobie.specs2._
import doobie.implicits._
import cats.implicits._
import cats.effect.IO

import _root_.io.chrisdavenport.fuuid.FUUID

class PostgresInstanceSpec extends mutable.Specification with IOChecker with BeforeAll {
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres", ""
  )

  def beforeAll(): Unit = {
    sql"""
    CREATE TABLE PostgresInstanceSpec (
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