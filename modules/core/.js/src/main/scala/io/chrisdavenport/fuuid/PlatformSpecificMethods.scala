package io.chrisdavenport.fuuid

import cats.ApplicativeError

object PlatformSpecificMethods {
  def nameBased[F[_]]: (FUUID, String, ApplicativeError[F, Throwable]) => F[FUUID] = (_, _, AE) =>
    AE.raiseError(new NotImplementedError("Name based UUIDs are not supported for Scala.js"))
}
