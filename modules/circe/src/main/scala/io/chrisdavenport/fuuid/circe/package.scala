package io.chrisdavenport.fuuid

import io.circe.syntax._
import io.circe.{Decoder, Encoder, KeyDecoder, KeyEncoder}
import cats.implicits._

package object circe {
  implicit val fuuidEncoder: Encoder[FUUID] = Encoder.instance(_.show.asJson)
  implicit val fuuidDecoder: Decoder[FUUID] =
    Decoder[String].emap(FUUID.fromString(_).leftMap(_.toString))

  implicit val fuuidKeyEncoder: KeyEncoder[FUUID] = KeyEncoder.instance(_.show)
  implicit val fuuidKeyDecoder: KeyDecoder[FUUID] =
    KeyDecoder.instance(FUUID.fromString(_).toOption)
}
