# fuuid - Functional UUID [![Build Status](https://travis-ci.com/ChristopherDavenport/fuuid.svg?branch=master)](https://travis-ci.com/ChristopherDavenport/fuuid) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.12)

Head on [over to the microsite](https://davenverse.github.io/fuuid/)

## Impatient Quickstart

To use fuuid in an existing SBT project with Scala 2.11 or a later version, add the following dependency to your
`build.sbt`:

```scala
libraryDependencies += "io.chrisdavenport" %% "fuuid" % "<version>"
```

And for integrations:
```scala
libraryDependencies ++= Seq(
    "io.chrisdavenport" %% "fuuid-circe"  % "<version>", // Circe integration
    "io.chrisdavenport" %% "fuuid-http4s" % "<version>", // Http4s integration
    "io.chrisdavenport" %% "fuuid-doobie" % "<version>"  // Doobie integration
)
```

For more info visit [the microsite](https://davenverse.github.io/fuuid/)
