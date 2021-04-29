package io.chrisdavenport.fuuid

import cats._
import cats.implicits._
import java.util.UUID
import java.security.MessageDigest

object PlatformSpecificMethods {
  def nameBased[F[_]]: (FUUID, String, ApplicativeError[F, Throwable]) => F[FUUID] =
    (namespace, name, AE) =>
      Either
        .catchNonFatal(
          MessageDigest
            .getInstance("SHA-1")
            .digest(
              uuidBytes(namespace) ++
                name.getBytes("UTF-8")
            )
        )
        .map { bs =>
          val cs =
            bs.take(16) // Truncate 160 bits (20 bytes) SHA-1 to fit into our 128 bits of space
          cs(6) = (cs(6) & 0x0f).asInstanceOf[Byte] /* clear version                */
          cs(6) = (cs(6) | 0x50).asInstanceOf[Byte] /* set to version 5, SHA1 UUID  */
          cs(8) = (cs(8) & 0x3f).asInstanceOf[Byte] /* clear variant                */
          cs(8) = (cs(8) | 0x80).asInstanceOf[Byte] /* set to IETF variant          */
          cs
        }
        .flatMap(bs =>
          Either.catchNonFatal {
            val bb = java.nio.ByteBuffer.allocate(java.lang.Long.BYTES)
            bb.put(bs, 0, 8)
            bb.flip
            val most = bb.getLong
            bb.flip
            bb.put(bs, 8, 8)
            bb.flip
            val least = bb.getLong
            FUUID.fromUUID(new UUID(most, least))
          }
        )
        .liftTo[F](AE)

  private def uuidBytes(fuuid: FUUID): Array[Byte] = {
    val bb = java.nio.ByteBuffer.allocate(2 * java.lang.Long.BYTES)
    val uuid = FUUID.Unsafe.toUUID(fuuid)
    bb.putLong(uuid.getMostSignificantBits)
    bb.putLong(uuid.getLeastSignificantBits)
    bb.array
  }
}
