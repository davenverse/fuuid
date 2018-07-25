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
    CREATE TABLE person (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  check(sql"SELECT id from person".query[FUUID])
  check(sql"INSERT into person (id) VALUES ${FUUID.randomFUUID[IO].unsafeRunSync}".update)
  
}