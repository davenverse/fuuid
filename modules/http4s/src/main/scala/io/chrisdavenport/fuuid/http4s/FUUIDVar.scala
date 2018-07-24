package io.chrisdavenport.fuuid.http4s

import io.chrisdavenport.fuuid.FUUID

object FUUIDVar {
  def unapply(str: String): Option[FUUID] =
    if(!str.isEmpty)
      FUUID.fromString(str).toOption
    else
      None
}
