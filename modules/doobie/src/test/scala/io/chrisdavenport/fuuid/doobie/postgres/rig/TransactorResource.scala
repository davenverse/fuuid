package io.chrisdavenport.fuuid.doobie.postgres.rig

import cats.implicits._
import cats.effect._
import doobie._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import _root_.io.chrisdavenport.whaletail.{
  Docker,
  Containers,
  Images
}
import _root_.io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import scala.concurrent.duration._
import java.net.ServerSocket

object TransactorResource {
  lazy val logger = Slf4jLogger.getLogger[IO]
  lazy val dbName = "db"
  lazy val dbUserName = "user"
  lazy val dbPassword = "password"
  lazy val driverName = "org.postgresql.Driver"

  def create(implicit CS: ContextShift[IO], T: Timer[IO]): Resource[IO,Transactor[IO]] = {
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
          Containers.Operations.logs(client, created.id).flatMap{ log => 
              {
                if (log.contains("database system is ready to accept connections")) 
                  Timer[IO].sleep(1.seconds)
                else Timer[IO].sleep(0.3.seconds) >> action
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
    } yield transactor
  }
}