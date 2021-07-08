package io.chrisdavenport.fuuid.doobie.h2

import cats.effect.IO
import munit.CatsEffectSuite
import doobie._
import doobie.h2.implicits._
import doobie.implicits._
import doobie.munit.IOChecker
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._

class H2InstanceSpec extends CatsEffectSuite with IOChecker {
  lazy val transactor: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "org.h2.Driver",
      url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      user = "sa",
      pass = ""
    )

  override def beforeAll(): Unit =
    sql"CREATE TABLE test (id UUID NOT NULL)".update.run.transact(transactor).void.unsafeRunSync()

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into test (id) VALUES ($fuuid)""".update

  val fuuid = U.unsafeRunSync(FUUID.randomFUUID[IO])

  test("Select from h2") {
    check(sql"SELECT id from test".query[FUUID])
  }
  test("insert uuid") {
    check(insertId(fuuid))
  }
}
