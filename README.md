# fuuid - Functional UUID [![Build Status](https://travis-ci.com/ChristopherDavenport/fuuid.svg?branch=master)](https://travis-ci.com/ChristopherDavenport/fuuid) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12)

Java UUID's aren't exceptionally safe. Operations throw and are not
referentially transparent. We can fix that.

Currently this is the minimal implementation that keeps me content.

## Quick Start

To use fuuid in an existing SBT project with Scala 2.11 or a later version, add the following dependency to your
`build.sbt`:

```scala
libraryDependencies += "io.chrisdavenport" %% "fuuid" % "<version>"
```

## Examples

```scala
import io.chrisdavenport.fuuid.FUUID
import cats.effect.IO

// We place it in IO because this requires a Sync constraint
val create: IO[FUUID] = FUUID.randomFUUID[IO]

val fromString : Either[IllegalArgumentException, FUUID] = FUUID.fromString("d6faceab-4193-4508-86ca-e1561d38fea6")

val failsReferentiallyTransparently : Either[IllegalArgumentException, FUUID] = FUUID.fromString("Not a UUID")
// Will be a Left

// For some syntax improvements
import cats.implicits._

// Uses cats Eq
val equalToItself : IO[Boolean] = for {
  fuuid <- FUUID.randomFUUID[IO]
} yield fuuid === fuuid

equalToItself.unsafeRunSync
// true

// Uses cats Order
val laterGreaterThanEarlier : IO[Boolean] = for {
  fuuid1 <- FUUID.randomFUUID[IO]
  fuuid2 <- FUUID.randomFUUID[IO]
} yield fuuid2 > fuuid1
// true
```
