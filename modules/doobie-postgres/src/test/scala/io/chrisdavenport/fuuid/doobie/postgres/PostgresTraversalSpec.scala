package io.chrisdavenport.fuuid.doobie.postgres

import org.specs2._
import org.specs2.specification.BeforeAll
import doobie._
import doobie.implicits._
import cats.implicits._
import cats.effect.IO

import _root_.io.chrisdavenport.fuuid._

class PostgresTraversalSpec extends mutable.Specification 
  with BeforeAll with ScalaCheck with FUUIDArbitraries {
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres", ""
  )

  def beforeAll(): Unit = {
    sql"""
    CREATE TABLE PostgresTraversalSpec (
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

  "Doobie Meta" should {
    "traverse input and then extraction" in prop { fuuid: FUUID => 

      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      action.unsafeRunSync must_=== fuuid
    }
  } 
  
}