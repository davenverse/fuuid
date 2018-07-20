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
  implicit val orderFUUID: Order[FUUID] = Order.from{ case (f1, f2) => 
    f1.uuid.compareTo(f2.uuid)
  }
  implicit val hashFUUID: Hash[FUUID] = new Hash[FUUID]{
    def eqv(x: FUUID, y: FUUID): Boolean = x.eqv(y)
    def hash(x: FUUID): Int = x.hashCode
  }

  def fromString(s: String): Either[IllegalArgumentException, FUUID] = 
    Either.catchOnly[IllegalArgumentException](new FUUID(UUID.fromString(s)))

  def fromUUID(uuid: UUID): FUUID = new FUUID(uuid)

  def randomFUUID[F[_]: Sync]: F[FUUID] = Sync[F].delay(
    new FUUID(UUID.randomUUID)
  )

}