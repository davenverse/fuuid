package io.chrisdavenport.fuuid.doobie

import java.util.UUID

import doobie.Meta
import doobie.util.{Get, Put}
import io.chrisdavenport.fuuid.FUUID

object implicits {

  @deprecated(message = "Please use FuuidMeta instead.", since = "0.5.1")
  def FuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    FuuidMeta(Get[FUUID], Put[FUUID])

  implicit def FuuidGet(implicit G: Get[UUID]): Get[FUUID] = G.map(FUUID.fromUUID)

  implicit def FuuidPut(implicit P: Put[UUID]): Put[FUUID] = P.contramap(FUUID.Unsafe.toUUID)

  implicit def FuuidMeta(implicit A: Get[FUUID], B: Put[FUUID]): Meta[FUUID] = Meta[FUUID]

}
