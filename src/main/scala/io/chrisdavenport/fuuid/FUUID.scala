package io.chrisdavenport.fuuid

import cats._
import cats.implicits._
import cats.effect.Sync
import java.util.UUID

final class FUUID private (private val uuid: UUID){

  // Returns 0 when equal
  def eqv(that: FUUID): Boolean = this.uuid.compareTo(that.uuid) == 0

  // Please god don't use this
  override def equals(obj: scala.Any): Boolean = obj match {
    case that: FUUID => eqv(that)
    case _ => false
  }
  override def hashCode: Int = uuid.hashCode
  override def toString: String = uuid.toString

}

object FUUID {
  implicit val showFUUID: Show[FUUID] = Show.show[FUUID](_.toString)
  implicit val relationalFUUID: Hash[FUUID] with Order[FUUID] = new Hash[FUUID] with Order[FUUID]{
    override def eqv(x: FUUID, y: FUUID): Boolean = x.eqv(y)
    override def hash(x: FUUID): Int = x.hashCode
    override def compare(x: FUUID, y: FUUID): Int = x.uuid.compareTo(y.uuid)
  }

  def fromString(s: String): Either[IllegalArgumentException, FUUID] = 
    Either.catchOnly[IllegalArgumentException](new FUUID(UUID.fromString(s)))

  def fromUUID(uuid: UUID): FUUID = new FUUID(uuid)

  def randomFUUID[F[_]: Sync]: F[FUUID] = Sync[F].delay(
    new FUUID(UUID.randomUUID)
  )

}