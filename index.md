
# fuuid - Functional UUID [![Build Status](https://travis-ci.com/ChristopherDavenport/fuuid.svg?branch=master)](https://travis-ci.com/ChristopherDavenport/fuuid) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12)

Java UUID's aren't "exceptionally" safe. Operations throw and are not
referentially transparent. We can fix that.

Currently this is the minimal implementation that keeps me content. This aims to offer a baseline
set of tools for interacting with FUUID's. Modules add functionality by bringing in additional
dependencies to support additional interactions.

## Quick Start

To use fuuid in an existing SBT project with Scala 2.11 or a later version, add the following dependency to your
`build.sbt`:

```scala
libraryDependencies += "io.chrisdavenport" %% "fuuid" % "<version>"
```

## Examples

```scala
import io.chrisdavenport.fuuid.FUUID
// import io.chrisdavenport.fuuid.FUUID

import cats.effect.IO
// import cats.effect.IO

// We place it in IO because this requires a Sync constraint
val create: IO[FUUID] = FUUID.randomFUUID[IO]
// create: cats.effect.IO[io.chrisdavenport.fuuid.FUUID] = IO$1420027800

val fromString : Either[IllegalArgumentException, FUUID] = FUUID.fromString("d6faceab-4193-4508-86ca-e1561d38fea6")
// fromString: Either[IllegalArgumentException,io.chrisdavenport.fuuid.FUUID] = Right(d6faceab-4193-4508-86ca-e1561d38fea6)

val failsReferentiallyTransparently : Either[IllegalArgumentException, FUUID] = FUUID.fromString("Not a UUID")
// failsReferentiallyTransparently: Either[IllegalArgumentException,io.chrisdavenport.fuuid.FUUID] = Left(java.lang.IllegalArgumentException: Invalid UUID string: Not a UUID)

// For some syntax improvements
import cats.implicits._
// import cats.implicits._

// Uses cats Eq
val equalToItself : IO[Boolean] = for {
  fuuid <- FUUID.randomFUUID[IO]
} yield fuuid === fuuid
// equalToItself: cats.effect.IO[Boolean] = <function1>

equalToItself.unsafeRunSync
// res3: Boolean = true

// Uses cats Order
val laterGreaterThanEarlier : IO[Boolean] = for {
  fuuid1 <- FUUID.randomFUUID[IO]
  fuuid2 <- FUUID.randomFUUID[IO]
} yield fuuid2 > fuuid1
// laterGreaterThanEarlier: cats.effect.IO[Boolean] = IO$842309317

laterGreaterThanEarlier.unsafeRunSync
// res5: Boolean = true
```
