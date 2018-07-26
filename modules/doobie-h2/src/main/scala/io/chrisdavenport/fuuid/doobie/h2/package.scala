package io.chrisdavenport.fuuid.doobie

import doobie.util.meta.Meta
import doobie.h2.implicits.UuidType
import io.chrisdavenport.fuuid.FUUID

package object h2 {

  implicit val FuuidType: Meta[FUUID] =
    UuidType.xmap[FUUID](FUUID.fromUUID, fuuid => FUUID.Unsafe.toUUID(fuuid))
}
