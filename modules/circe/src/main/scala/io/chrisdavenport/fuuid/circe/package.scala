package io.chrisdavenport.fuuid

import io.circe.syntax._
import io.circe.{Decoder, Encoder}

package object circe {
  implicit val encoder: Encoder[FUUID] = Encoder.instance(_.toString.asJson)
  implicit val decoder: Decoder[FUUID] =
    Decoder[String].emap(FUUID.fromString(_).left.map(_.toString))
}
