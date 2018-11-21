package io.chrisdavenport.fuuid.http4s

import io.chrisdavenport.fuuid.FUUID
import cats.data.ValidatedNel
import cats.syntax.either._
import org.http4s.QueryParamDecoder
import org.http4s.QueryParameterValue
import org.http4s.ParseFailure

object implicits {
  implicit val fuuidQueryParamDecoder: QueryParamDecoder[FUUID] =
    new QueryParamDecoder[FUUID] {
      def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, FUUID] =
        FUUID
          .fromString(value.value)
          .leftMap(
            _ =>
              ParseFailure(
                "Failed to parse FUUID query parameter",
                s"Could not parse ${value.value} as a FUUID"))
          .toValidatedNel
    }
}
