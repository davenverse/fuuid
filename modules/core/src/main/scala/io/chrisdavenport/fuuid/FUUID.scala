package io.chrisdavenport.fuuid

import cats._
import cats.implicits._
import cats.effect.Sync
import java.util.UUID

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

final class FUUID private (private val uuid: UUID){

  // Direct show method so people do not use toString
  def show: String = uuid.show
  // -1 less than, 0 equal to, 1 greater than
  def compare(that: FUUID): Int = this.uuid.compareTo(that.uuid)

  // Returns 0 when equal
  def eqv(that: FUUID): Boolean = compare(that) == 0

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: FUUID => eqv(that)
    case _ => false
  }
  override def hashCode: Int = uuid.hashCode
  override def toString: String = uuid.toString

}

object FUUID {
  implicit val instancesFUUID: Hash[FUUID] with Order[FUUID] with Show[FUUID] =
    new Hash[FUUID] with Order[FUUID] with Show[FUUID]{
      override def show(t: FUUID): String = t.show
      override def eqv(x: FUUID, y: FUUID): Boolean = x.eqv(y)
      override def hash(x: FUUID): Int = x.hashCode
      override def compare(x: FUUID, y: FUUID): Int = x.compare(y)
    }

  def fromString(s: String): Either[Throwable, FUUID] =
    Either.catchNonFatal(new FUUID(UUID.fromString(s)))

  def fromStringOpt(s: String): Option[FUUID] = 
    fromString(s).toOption

  def fromStringF[F[_]](s: String)(implicit AE: ApplicativeError[F, Throwable]): F[FUUID] =
    fromString(s).fold(AE.raiseError, AE.pure)

  def fromUUID(uuid: UUID): FUUID = new FUUID(uuid)

  def randomFUUID[F[_]: Sync]: F[FUUID] = Sync[F].delay(
    new FUUID(UUID.randomUUID)
  )

  def fuuid(s: String): FUUID = macro Macros.fuuidLiteral

  private[FUUID] class Macros(val c: whitebox.Context) {
    import c.universe._
    def fuuidLiteral(s: c.Expr[String]): Tree =
      s.tree match {
        case Literal(Constant(s: String))=>
            fromString(s)
            .fold(
              e => c.abort(c.enclosingPosition, e.getMessage),
              _ =>
                q"_root_.io.chrisdavenport.fuuid.FUUID.fromString($s).fold(throw _, _root_.scala.Predef.identity)"
            )
        case _ =>
          c.abort(
            c.enclosingPosition,
            s"This method uses a macro to verify that a FUUID literal is valid. Use FUUID.fromString if you have a dynamic value you want to parse as an FUUID."
          )
      }
  }

  /**
    * A Home For functions we don't trust
    * Hopefully making it very clear that this code needs
    * to be dealt with carefully.
    *
    * Likely necessary for some interop
    *
    * Please do not import directly but prefer `FUUID.Unsafe.xxx`
    **/
  object Unsafe {
    def toUUID(fuuid: FUUID): UUID = fuuid.uuid
    def withUUID[A](fuuid: FUUID)(f: UUID => A): A = f(fuuid.uuid)
  }

}
