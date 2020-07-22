package io.chrisdavenport.fuuid.doobie.postgres.rig

import cats.implicits._
import cats.effect._
import cats.effect.concurrent._
import org.specs2.specification.BeforeAfterAll
import cats.effect.syntax.effect._
import scala.concurrent.duration._
import org.specs2.execute.{AsResult, Result}

import cats.effect.testing.specs2.CatsEffect
import cats.effect.testing.specs2.CatsIO

trait CatsResource[F[_], A] extends BeforeAfterAll with CatsEffect { self => 
  
  def resource: Resource[F, A]

  implicit def ResourceEffect: ConcurrentEffect[F]
  protected val ResourceTimeout: Duration = 20.seconds

  private var shutdown : F[Unit] = ResourceEffect.unit
  private val started : Deferred[F, A] = Deferred.unsafe[F, A] 
  def getA: F[A] = started.get

  override def beforeAll(): Unit = {
    ResourceEffect.map(resource.allocated){ case (a, shutdownAction) => 
      shutdown = shutdownAction
      a
    }.flatTap(a => started.complete(a)).toIO.unsafeRunTimed(ResourceTimeout)
    ()
  }
  override def afterAll(): Unit = {
    shutdown.toIO.unsafeRunTimed(ResourceTimeout)
    shutdown = ResourceEffect.unit
  }

  def withResource[R](r: A => R)(implicit R: AsResult[R]): Result = {
    self.effectAsResult[F, R].asResult((getA.map(r)))
  }

}

trait CatsResourceIO[A] extends CatsIO with CatsResource[IO, A]{ self => 

  implicit def ResourceEffect: ConcurrentEffect[IO] = 
    IO.ioConcurrentEffect
  
  def resource: Resource[IO, A]

}