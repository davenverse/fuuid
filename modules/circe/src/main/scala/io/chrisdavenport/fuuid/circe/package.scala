package io.chrisdavenport.fuuid

import cats.syntax.show._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

package object circe {
  implicit val fuuidEncoder: Encoder[FUUID] = Encoder.instance(_.show.asJson)
  implicit val fuuidDecoder: Decoder[FUUID] =
    Decoder[String].emap(FUUID.fromString(_).left.map(_.toString))
}
