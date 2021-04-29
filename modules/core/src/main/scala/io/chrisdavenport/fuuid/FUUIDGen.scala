package io.chrisdavenport.fuuid

import java.util.UUID
import cats.implicits._
import cats.effect.Sync

/**
 * This trait is an F-algebra representation of the ability to generate FUUID's.
 *
 * At some edge a Sync is required in order to populate the randomness when required.
 */
@scala.annotation.implicitNotFound("""Cannot find implicit value for FUUIDGen[${F}].
Building this implicit value depends on having an implicit
Sync[${F}] or some equivalent type.""")
trait FUUIDGen[F[_]] {

  /**
   * Creates a Random FUUID
   */
  def random: F[FUUID]

  /**
   * Creates an FUUID from a String, if it is valid
   */
  def fromString(s: String): F[FUUID]

  /**
   * Creates an FUUID from a UUID
   */
  def fromUUID(uuid: UUID): F[FUUID]

  /**
   * Creates a new name-based UUIDv5. NOTE: Not implemented for Scala.js!
   */
  def nameBased(namespace: FUUID, name: String): F[FUUID]
}

object FUUIDGen {
  def apply[F[_]](implicit ev: FUUIDGen[F]): FUUIDGen[F] = ev

  // Sync f => class FUUIDGen f
  implicit def instance[F[_]: Sync]: FUUIDGen[F] = new SyncFUUIDGen[F]

  private class SyncFUUIDGen[F[_]: Sync] extends FUUIDGen[F] {
    def random: F[FUUID] = FUUID.randomFUUID[F]
    def fromString(s: String): F[FUUID] = FUUID.fromStringF[F](s)
    def fromUUID(uuid: UUID): F[FUUID] = FUUID.fromUUID(uuid).pure[F]
    def nameBased(namespace: FUUID, name: String): F[FUUID] = FUUID.nameBased[F](namespace, name)
  }
}
