package io.chrisdavenport.fuuid.doobie.postgres

import org.specs2._
import doobie._
import cats.effect.IO

class PostgresInstanceSpec extends mutable.Specification {
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres", ""
  )
  
}