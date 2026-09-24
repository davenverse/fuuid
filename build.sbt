ThisBuild / tlBaseVersion := "0.8" // current series x.y

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / startYear := Some(2018)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport"),
  tlGitHubDev("JesusMtnez", "Jesús Martínez-B. H.")
)

// sbt-davenverse published a snapshot from main on every push; preserve that.
ThisBuild / tlCiReleaseBranches := Seq("main")

val Scala213 = "2.13.18"
val Scala212 = "2.12.20"
val Scala3 = "3.3.8"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / crossScalaVersions := Seq(Scala212, Scala213, Scala3)
ThisBuild / scalaVersion := Scala213

def crossCompileDirs(scalaVersion: String, baseDirectory: File) = {
  val major = CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, _)) => "-2"
    case _ => "-3"
  }
  List(CrossType.Pure, CrossType.Full).flatMap(
    _.sharedSrcDir(baseDirectory, "main").toList.map(f => file(f.getPath + major))
  )
}

lazy val fuuid = project
  .in(file("."))
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings)
  .aggregate(coreJS, coreJVM, doobie, http4s, circeJS, circeJVM)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(commonSettings)
  .settings(
    Compile / unmanagedSourceDirectories ++= crossCompileDirs(
      scalaVersion.value,
      baseDirectory.value
    )
  )
  .settings(
    name := "fuuid"
  )
  .jsSettings(
    // Newer Scala.js implements java.util.UUID.randomUUID via
    // java.security.SecureRandom, which is not in the Scala.js javalib. Without
    // this the JS build fails to link: "Referring to non-existent class
    // java.security.SecureRandom". Only published for 2.13, hence for3Use2_13.
    libraryDependencies += ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0")
      .cross(CrossVersion.for3Use2_13)
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val doobie = project
  .in(file("modules/doobie"))
  .settings(commonSettings)
  .settings(
    name := "fuuid-doobie",
    libraryDependencies ++= Seq(
      "org.tpolecat"                           %% "doobie-core"      % doobieV,
      "org.tpolecat"                           %% "doobie-postgres"  % doobieV         % Test,
      "org.tpolecat"                           %% "doobie-h2"        % doobieV         % Test,
      "org.tpolecat"                           %% "doobie-munit"     % doobieV         % Test,
      "org.typelevel"                          %% "discipline-munit" % disciplineMunit % Test,
      "org.scalameta"                          %% "munit"            % munitV          % Test,
      "org.scalameta"                          %% "munit-scalacheck" % munitV          % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % munitCE3V          % Test,
      ("com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersV % Test)
        .cross(CrossVersion.for3Use2_13)
    ),
    Test / parallelExecution := false // Needed due to a driver initialization deadlock between Postgres and H2
  )
  .dependsOn(coreJVM % "compile->compile;test->test")

lazy val circe = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/circe"))
  .settings(commonSettings)
  .settings(
    name := "fuuid-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeV
    )
  )
  .jsSettings(
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeV % Test
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val circeJS = circe.js
lazy val circeJVM = circe.jvm

lazy val http4s = project
  .in(file("modules/http4s"))
  .settings(commonSettings)
  .settings(
    name := "fuuid-http4s",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "org.http4s" %% "http4s-dsl"  % http4sV % Test
    )
  )
  .dependsOn(coreJVM % "compile->compile;test->test")

// Replaces DavenverseMicrositePlugin (sbt-microsites/Jekyll). mdocIn resolves to
// the repo-root docs/ directory, which is where index.md now lives.
lazy val site = project
  .in(file("modules/site"))
  .enablePlugins(TypelevelSitePlugin)
  .dependsOn(coreJVM, http4s, doobie, circeJVM)
  .settings(
    laikaTheme := tlSiteHelium.value.site
      .topNavigationBar(
        homeLink = laika.helium.config.IconLink.internal(laika.ast.Path.Root / "index.md", laika.helium.config.HeliumIcon.home)
      )
      .build
  )

val catsV = "2.7.0" //https://github.com/typelevel/cats/releases
val catsEffectV = "3.3.12" //https://github.com/typelevel/cats-effect/releases
val circeV = "0.14.1" //https://github.com/circe/circe/releases
val http4sV = "0.23.6" //https://github.com/http4s/http4s/releases
val doobieV = "1.0.0-RC1" //https://github.com/tpolecat/doobie/releases
val scalaJavaTimeV = "2.3.0" // https://github.com/cquiroz/scala-java-time/releases
val testcontainersV = "0.39.8"
val munitV = "0.7.29"
val munitCE3V = "1.0.6"
val disciplineMunit = "1.0.9"

// General Settings
lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-effect"         % catsEffectV,
    "org.typelevel" %%% "cats-laws"           % catsV           % Test,
    "org.typelevel" %%% "discipline-munit"    % disciplineMunit % Test,
    "org.scalameta" %%% "munit"               % munitV          % Test,
    "org.scalameta" %%% "munit-scalacheck"    % munitV          % Test,
    "org.typelevel" %%% "munit-cats-effect-3" % munitCE3V       % Test
  ),
  // Compiler settings DavenversePlugin injected globally. sbt-typelevel-ci-release
  // does not supply these (only sbt-typelevel-settings would).
  libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) => Nil
    case _ =>
      Seq(
        scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
        scalaOrganization.value % "scala-reflect"  % scalaVersion.value % Provided,
        compilerPlugin("org.typelevel" % "kind-projector"     % "0.13.4" cross CrossVersion.full),
        compilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1")
      )
  }),
  // fuuid defines a macro (FUUID.fuuid literal); sbt-tpolecat used to supply
  // -language:experimental.macros, and sbt-typelevel-ci-release does not.
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => Seq("-Ymacro-annotations", "-language:experimental.macros")
    case Some((2, 12)) => Seq("-Ypartial-unification", "-language:experimental.macros")
    case Some((3, _)) => Seq("-Ykind-projector")
    case _ => Nil
  }),
  Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
)
