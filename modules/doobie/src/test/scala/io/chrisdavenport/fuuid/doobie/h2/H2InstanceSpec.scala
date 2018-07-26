package io.chrisdavenport.fuuid.doobie.h2

import cats.effect.IO
import cats.syntax.functor._
import doobie._
import doobie.h2.H2Transactor
import doobie.h2.implicits._
import doobie.implicits._
import doobie.specs2._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class H2InstanceSpec extends Specification with IOChecker with BeforeAll {

  val transactor: Transactor[IO] =
    H2Transactor.newH2Transactor[IO](
      url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      user = "sa",
      pass = ""
    ).unsafeRunSync()

  def beforeAll(): Unit = {
    sql"CREATE TABLE test (id UUID NOT NULL)".update.run.transact(transactor).void.unsafeRunSync
  }

  def insertId(fuuid: FUUID): Update0 = {
    sql"""INSERT into test (id) VALUES ($fuuid)""".update
  }

  val fuuid = FUUID.randomFUUID[IO].unsafeRunSync

  check(sql"SELECT id from test".query[FUUID])
  check(insertId(fuuid))

}