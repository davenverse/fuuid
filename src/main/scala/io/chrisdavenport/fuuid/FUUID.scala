package io.chrisdavenport.fuuid

import cats._
import cats.implicits._
import cats.effect.Sync
import java.util.UUID

import com.eatthepath.uuid.FastUUID

final class FUUID private (private val uuid: UUID){
  def equals(that: FUUID): Boolean = 
    this.uuid equals that.uuid
  override def toString: String = uuid.toString

}

object FUUID {
  implicit val showFUUID: Show[FUUID] = Show.show[FUUID](x => FastUUID.toString(x.uuid))
  implicit val eqFUUID: Eq[FUUID] = Eq.instance[FUUID]{case (f1, f2) => f1.equals(f2)}
  implicit val orderFUUID: Order[FUUID] = Order.from{ case (f1, f2) => 
    f1.uuid.compareTo(f2.uuid)
  }

  def fromString(s: String): Either[IllegalArgumentException, FUUID] = 
    Either.catchOnly[IllegalArgumentException](new FUUID(FastUUID.parseUUID(s)))

  def fromUUID(uuid: UUID): FUUID = new FUUID(uuid)

  def randomFUUID[F[_]: Sync]: F[FUUID] = Sync[F].delay(
    new FUUID(UUID.randomUUID)
  )

}