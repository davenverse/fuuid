package io.chrisdavenport.fuuid.doobie

import doobie.Meta
import io.chrisdavenport.fuuid.FUUID

package object postgres {

  // FUUID
  implicit val FuuidType: Meta[FUUID] = Meta.other[FUUID]("uuid")
}