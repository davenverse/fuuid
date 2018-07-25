package io.chrisdavenport.fuuid.doobie

import doobie.Meta
import doobie.postgres.implicits.UuidType
import io.chrisdavenport.fuuid.FUUID

package object postgres {

  // FUUID
  implicit val FuuidType: Meta[FUUID] = 
    UuidType.xmap[FUUID](FUUID.fromUUID, fuuid => fuuid.uuid)
}