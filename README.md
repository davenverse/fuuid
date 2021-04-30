[![GitHub Actions](https://github.com/davenverse/fuuid/workflows/CI/badge.svg)](https://github.com/davenverse/fuuid/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/fuuid_2.13)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/davenverse/fuuid)](https://mergify.io)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

# fuuid - Functional UUID

Head on [over to the microsite](https://davenverse.github.io/fuuid/)

## Impatient Quickstart

To use fuuid in an existing SBT project with Scala 2.12 or a later version, add the following dependency to your
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
