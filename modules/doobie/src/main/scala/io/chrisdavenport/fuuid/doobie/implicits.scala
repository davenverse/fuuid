package io.chrisdavenport.fuuid.doobie

import java.util.UUID

import doobie.Meta
import doobie.util.{Get, Put}
import io.chrisdavenport.fuuid.FUUID

object implicits {

  @deprecated(
    message = "You should not demand a Meta implicitly. Please use FuuidGet or FuuidPut instead.",
    since = "0.5.1"
  )
  def FuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    FuuidMeta(Get[FUUID], Put[FUUID])

  implicit def FuuidGet(implicit G: Get[UUID]): Get[FUUID] = G.map(FUUID.fromUUID)

  implicit def FuuidPut(implicit P: Put[UUID]): Put[FUUID] = P.contramap(FUUID.Unsafe.toUUID)

  @deprecated(
    message = "You should not demand a Meta implicitly. Please use FuuidGet or FuuidPut instead.",
    since = "0.6.1"
  )
  def FuuidMeta(implicit A: Get[FUUID], B: Put[FUUID]): Meta[FUUID] =
    new Meta(A, B)
}
