package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import org.specs2._
import _root_.io.chrisdavenport.fuuid.doobie.postgres.rig._


class PostgresInstanceSpec extends mutable.Specification with CheckHelper with CatsResourceIO[Transactor[IO]]{
  sequential

  override def resource = TransactorResource.create
    .evalTap( transactor => 
        sql"""
      CREATE TABLE IF NOT EXISTS PostgresInstanceSpec (
        id   UUID NOT NULL
      )
      """.update.run.transact(transactor).void
    )

  def insertId(fuuid: FUUID): Update0 = {
    sql"""INSERT into PostgresInstanceSpec (id) VALUES ($fuuid)""".update
  }
  val fuuid = FUUID.randomFUUID[IO].unsafeRunSync

  check(sql"SELECT id from PostgresInstanceSpec".query[FUUID])
  check(insertId(fuuid))

}
