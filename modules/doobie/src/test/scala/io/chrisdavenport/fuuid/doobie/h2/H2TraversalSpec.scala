package io.chrisdavenport.fuuid.doobie.h2

import cats.effect.{ContextShift, IO}
import cats.implicits._
import doobie._
import doobie.h2.implicits._
import doobie.implicits._
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.fuuid._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll
import scala.concurrent.ExecutionContext.Implicits.global

class H2TraversalSpec extends Specification with BeforeAll with ScalaCheck with FUUIDArbitraries {

  implicit val contextShiftIO: ContextShift[IO] = IO.contextShift(global)

  lazy val transactor: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "org.h2.Driver",
      url = "jdbc:h2:mem:testH2Table;DB_CLOSE_DELAY=-1",
      user = "sa",
      pass = ""
    )

  def beforeAll(): Unit = {
    sql"""
    CREATE TABLE testH2Table (
      id   UUID NOT NULL
    )
    """.update.run.transact(transactor).void.unsafeRunSync()
  }

  def queryBy(fuuid: FUUID): Query0[FUUID] =
    sql"""SELECT id from testH2Table where id = ${fuuid}""".query[FUUID]

  def insertId(fuuid: FUUID): Update0 =
    sql"""INSERT into testH2Table (id) VALUES ($fuuid)""".update

  "Doobie H2 Meta" should {

    "traverse input and then extraction" in prop { fuuid: FUUID =>
      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      action.unsafeRunSync() must_=== fuuid
    }

    "fail on a non-present value" in prop { fuuid: FUUID =>
      queryBy(fuuid).unique
        .transact(transactor)
        .attempt
        .map(_.isLeft)
        .unsafeRunSync() must_=== true
    }
  }

}
