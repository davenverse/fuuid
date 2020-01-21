package io.chrisdavenport.fuuid.doobie

import java.util.UUID

import doobie.Meta
import io.chrisdavenport.fuuid.FUUID

object implicits {

  implicit def FuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    U.timap[FUUID](FUUID.fromUUID)(fuuid => FUUID.Unsafe.toUUID(fuuid))
}
