package io.chrisdavenport.fuuid.doobie.postgres

import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.fuuid._
// import io.chrisdavenport.testcontainersspecs2.ForAllTestContainer
// import com.dimafeng.testcontainers.GenericContainer
// import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
// import java.time.Duration
// import java.time.temporal.ChronoUnit.SECONDS
import org.specs2._
// import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect._
import _root_.io.chrisdavenport.whaletail.{
  Docker,
  Containers,
  Images
}
import _root_.io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import scala.concurrent.duration._
import java.net.ServerSocket

class PostgresTraversalSpec extends mutable.Specification
  with ScalaCheck with FUUIDArbitraries with CatsResourceIO[Transactor[IO]] {
  sequential

  val logger = Slf4jLogger.getLogger[IO]
  lazy val dbName = "db"
  lazy val dbUserName = "user"
  lazy val dbPassword = "password"
  lazy val driverName = "org.postgresql.Driver"

  override def resource: Resource[IO,Transactor[IO]] = {
    for {
      blocker <- Blocker[IO]
      client = Docker.default(blocker, logger)
      _ <- Resource.liftF(
        Images.Operations.createFromImage(client, "postgres", "latest".some)
      )
      port <- Resource.liftF(IO(new ServerSocket(0))).evalMap{ss => 
        val port = ss.getLocalPort
        IO(ss.close).as(port)
      }
      created <- Resource.liftF(
        Containers.Operations.create(
          client,
          "postgres:latest",
          Map(5432 -> port), 
          Map(
            "POSTGRES_DB" -> dbName,
            "POSTGRES_USER" -> dbUserName,
            "POSTGRES_PASSWORD" -> dbPassword
          ),
        )
      )
      _ <- Resource.make(
        Containers.Operations.start(client, created.id)
      )(_ => 
        Containers.Operations.stop(client, created.id, None).void
      )
      _ <- Resource.liftF(
        Containers.Operations.inspect(client, created.id)
      )

      _ <- Resource.liftF{
        def action: IO[Unit] = 
          Timer[IO].sleep(2.seconds) >> 
          Containers.Operations.logs(client, created.id).flatMap{ log => 
              {
                if (log.contains("database system is ready to accept connections")) IO.unit
                else action
              }
          }
        action
      }
      jdbcUrl = s"jdbc:postgresql://localhost:${port}/${dbName}"
      transactor = Transactor.fromDriverManager[IO](
        driverName,
        jdbcUrl,
        dbUserName,
        dbPassword
      )
      _ <- Resource.liftF(
      sql"""
          CREATE TABLE IF NOT EXISTS PostgresTraversalSpec (
            id   UUID NOT NULL
          )
        """.update.run.transact(transactor).void
      )
    } yield transactor
  }

  def queryBy(fuuid: FUUID): Query0[FUUID] = {
    sql"""SELECT id from PostgresTraversalSpec where id = ${fuuid}""".query[FUUID]
  }

  def insertId(fuuid: FUUID): Update0 = {
    sql"""INSERT into PostgresTraversalSpec (id) VALUES ($fuuid)""".update
  }

  "Doobie Postgres Meta" should {
    "traverse input and then extraction" in withResource{ transactor => prop { fuuid: FUUID =>

      val action = for {
        _ <- insertId(fuuid).run.transact(transactor)
        fuuid <- queryBy(fuuid).unique.transact(transactor)
      } yield fuuid

      action.unsafeRunSync must_=== fuuid
    }}
    "fail on a non-present value" in withResource{ transactor => prop { fuuid: FUUID =>
      queryBy(fuuid)
        .unique
        .transact(transactor)
        .attempt
        .map(_.isLeft)
        .unsafeRunSync must_=== true
    }}
  }

}
