package io.chrisdavenport.fuuid.doobie.postgres

import cats.effect._
import org.specs2.specification.BeforeAfterAll
import cats.effect.syntax.effect._
import scala.concurrent.duration._
import org.specs2.execute.{AsResult, Failure, Result}

import cats.effect.testing.specs2.CatsIO

trait CatsResource[F[_], A] extends BeforeAfterAll {
  
  def resource: Resource[F, A]

  implicit def ResourceEffect: Effect[F]
  protected val ResourceTimeout: Duration = 20.seconds

  private var value : Option[A] = None
  private var shutdown : F[Unit] = ResourceEffect.unit

  override def beforeAll(): Unit = {
    ResourceEffect.map(resource.allocated){ case (a, shutdownAction) => 
      value = Some(a)
      shutdown = shutdownAction
    }.toIO.unsafeRunTimed(ResourceTimeout)
    ()
  }
  override def afterAll(): Unit = {
    shutdown.toIO.unsafeRunTimed(ResourceTimeout)
    value = None
    shutdown = ResourceEffect.unit
  }

  def withResource[R](r: A => R)(implicit R: AsResult[R]): Result = {
    value.fold[Result](
      Failure("Resource Not Initialized When Trying to Use")
    )(a => 
      R.asResult(r(a))
    )

  }
}

trait CatsResourceIO[A] extends CatsResource[IO, A] with CatsIO with ResourceResult {

  implicit def ResourceEffect: Effect[IO] = IO.ioEffect
  
  def resource: Resource[IO, A]

}

trait ResourceResult {

  protected def ResourceResultTimeout =  10.seconds
  
  implicit def resourceAsResult[F[_]: Effect, R](implicit R: AsResult[R]): AsResult[Resource[F,R]] = new AsResult[Resource[F,R]]{
    def asResult(t: => Resource[F, R]): Result = 
      t.use(r => Sync[F].delay(R.asResult(r)))
        .toIO
        .unsafeRunTimed(ResourceResultTimeout)
        .getOrElse(Failure(s"expectation timed out after $ResourceResultTimeout"))
  } 

}