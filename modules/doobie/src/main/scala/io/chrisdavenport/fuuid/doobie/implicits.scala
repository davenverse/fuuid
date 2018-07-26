package io.chrisdavenport.fuuid.doobie

import java.util.UUID

import doobie.util.meta.Meta
import io.chrisdavenport.fuuid.FUUID

object implicits {

  implicit def FuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    U.xmap[FUUID](FUUID.fromUUID, fuuid => FUUID.Unsafe.toUUID(fuuid))
}
