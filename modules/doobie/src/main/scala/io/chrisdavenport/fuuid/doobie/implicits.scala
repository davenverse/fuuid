package io.chrisdavenport.fuuid.doobie

import java.util.UUID

import doobie.Meta
import io.chrisdavenport.fuuid.FUUID

object implicits {

  @deprecated(message = "Please use FuuidMeta instead.", since = "0.5.1")
  def FuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    FuuidMeta(U, U)

  implicit def FuuidMeta(implicit A: Get[UUID], B: Put[UUID]): Meta[FUUID] =
    U.timap[FUUID](FUUID.fromUUID)(fuuid => FUUID.Unsafe.toUUID(fuuid))
}
