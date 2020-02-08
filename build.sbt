import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val fuuid = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .settings(commonSettings, releaseSettings, skipOnPublishSettings)
  .aggregate(coreJS, coreJVM, doobie, http4s, circeJS, circeJVM/*, docs*/)

lazy val core = crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/core"))
    .settings(commonSettings, releaseSettings, mimaSettings)
    .settings(
      name := "fuuid"
    )


lazy val coreJS     = core.js
lazy val coreJVM    = core.jvm

lazy val doobie = project.in(file("modules/doobie"))
  .settings(commonSettings, releaseSettings, mimaSettings)
  .settings(
    name := "fuuid-doobie",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"     % doobieV,
      "org.tpolecat" %% "doobie-postgres" % doobieV % Test,
      "org.tpolecat" %% "doobie-h2"       % doobieV % Test,
      "org.tpolecat" %% "doobie-specs2"   % doobieV % Test,
      "io.chrisdavenport"           %% "testcontainers-specs2" % testContainersSpecs2V % Test
    ),
    parallelExecution in Test := false // Needed due to a driver initialization deadlock between Postgres and H2
  )
  .dependsOn(coreJVM % "compile->compile;test->test")

lazy val circe = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/circe"))
  .settings(commonSettings, releaseSettings, mimaSettings)
  .settings(
    name := "fuuid-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeV
    )
  )
  .jsSettings(
    libraryDependencies += "io.circe" %%% "not-java-time" % "0.2.0" % Test
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val circeJS = circe.js
lazy val circeJVM = circe.jvm


lazy val http4s = project.in(file("modules/http4s"))
  .settings(commonSettings, releaseSettings, mimaSettings)
  .settings(
    name := "fuuid-http4s",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "org.http4s" %% "http4s-dsl" % http4sV % Test
    )
  )
  .dependsOn(coreJVM % "compile->compile;test->test")

lazy val docs = project.in(file("modules/docs"))
  .disablePlugins(MimaPlugin)
  .settings(commonSettings, skipOnPublishSettings, micrositeSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"   %% "http4s-dsl"      % http4sV,
      "org.tpolecat" %% "doobie-postgres" % doobieV,
      "org.tpolecat" %% "doobie-h2"       % doobieV
    )
  )
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(TutPlugin)
  .dependsOn(coreJVM, http4s, doobie, circeJVM)

val catsV = "2.1.0"            //https://github.com/typelevel/cats/releases
val catsEffectV = "2.0.0"      //https://github.com/typelevel/cats-effect/releases
val specs2V = "4.8.3"             //https://github.com/etorreborre/specs2/releases
val disciplineSpecs2V = "1.0.0" 
val circeV = "0.13.0"          //https://github.com/circe/circe/releases
val http4sV = "0.21.0-RC2"         //https://github.com/http4s/http4s/releases
val doobieV = "0.8.8"          //https://github.com/tpolecat/doobie/releases
val scalaJavaTimeV = "2.0.0-RC3"  // https://github.com/cquiroz/scala-java-time/releases
val testContainersSpecs2V = "0.2.0-M2" // 

lazy val contributors = Seq(
  "ChristopherDavenport" -> "Christopher Davenport",
  "JesusMtnez" -> "Jesús Martínez-B. H."
)

// General Settings
lazy val commonSettings = Seq(
  organization := "io.chrisdavenport",

  scalaVersion := "2.13.0",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.8"),

  scalacOptions += "-Yrangepos",

  addCompilerPlugin("org.typelevel" %  "kind-projector"     % "0.10.3" cross CrossVersion.binary),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  libraryDependencies ++= Seq(
    "org.scala-lang"              %  "scala-reflect"               % scalaVersion.value,
    "org.typelevel"               %%% "cats-effect"                % catsEffectV,

    "org.typelevel"               %%% "cats-laws"                  % catsV         % Test,
    "org.typelevel"               %%% "discipline-specs2"          % disciplineSpecs2V % Test,
    "org.specs2"                  %%% "specs2-core"                % specs2V       % Test,
    "org.specs2"                  %%% "specs2-scalacheck"          % specs2V       % Test
  )
)

lazy val releaseSettings = {
  Seq(
    publishArtifact in Test := false,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/ChristopherDavenport/fuuid"),
        "git@github.com:ChristopherDavenport/fuuid.git"
      )
    ),
    homepage := Some(url("https://github.com/ChristopherDavenport/fuuid")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    publishMavenStyle := true,
    pomIncludeRepository := { _ =>
      false
    },
    pomExtra := {
      <developers>
        {for ((username, name) <- contributors) yield
        <developer>
          <id>{username}</id>
          <name>{name}</name>
          <url>http://github.com/{username}</url>
        </developer>
        }
      </developers>
    }
  )
}

lazy val micrositeSettings = Seq(
  micrositeName := "fuuid",
  micrositeDescription := "Functional UUID's",
  micrositeAuthor := "Christopher Davenport",
  micrositeGithubOwner := "ChristopherDavenport",
  micrositeGithubRepo := "fuuid",
  micrositeBaseUrl := "/fuuid",
  micrositeDocumentationUrl := "https://christopherdavenport.github.io/fuuid",
  micrositeFooterText := None,
  micrositeHighlightTheme := "atom-one-light",
  micrositePalette := Map(
    "brand-primary" -> "#3e5b95",
    "brand-secondary" -> "#294066",
    "brand-tertiary" -> "#2d5799",
    "gray-dark" -> "#49494B",
    "gray" -> "#7B7B7E",
    "gray-light" -> "#E5E5E6",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"
  ),
  fork in tut := true,
  scalacOptions in Tut --= Seq(
    "-Xfatal-warnings",
    "-Ywarn-unused-import",
    "-Ywarn-numeric-widen",
    "-Ywarn-dead-code",
    "-Ywarn-unused:imports",
    "-Xlint:-missing-interpolator,_"
  ),
  libraryDependencies += "com.47deg" %% "github4s" % "0.20.1",
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := sys.env.get("GITHUB_TOKEN")
)

lazy val mimaSettings = {

  def semverBinCompatVersions(major: Int, minor: Int, patch: Int): Set[(Int, Int, Int)] = {
    val majorVersions: List[Int] = List(major)
    val minorVersions : List[Int] =
      if (major >= 1) Range(0, minor).inclusive.toList
      else List(minor)
    def patchVersions(currentMinVersion: Int): List[Int] =
      if (minor == 0 && patch == 0) List.empty[Int]
      else if (currentMinVersion != minor) List(0)
      else Range(0, patch - 1).inclusive.toList

    val versions = for {
      maj <- majorVersions
      min <- minorVersions
      pat <- patchVersions(min)
    } yield (maj, min, pat)
    versions.toSet
  }

  def mimaVersions(version: String): Set[String] = {
    VersionNumber(version) match {
      case VersionNumber(Seq(major, minor, patch, _*), _, _) if patch.toInt > 0 =>
        semverBinCompatVersions(major.toInt, minor.toInt, patch.toInt)
          .map{case (maj, min, pat) => maj.toString + "." + min.toString + "." + pat.toString}
      case _ =>
        Set.empty[String]
    }
  }
  // Safety Net For Exclusions
  lazy val excludedVersions: Set[String] = Set()

  // Safety Net for Inclusions
  lazy val extraVersions: Set[String] = Set()

  Seq(
    mimaFailOnProblem := mimaVersions(version.value).toList.headOption.isDefined,
    mimaPreviousArtifacts := (mimaVersions(version.value) ++ extraVersions)
      .filterNot(excludedVersions.contains(_))
      .map{v =>
        val moduleN = moduleName.value + "_" + scalaBinaryVersion.value.toString
        organization.value % moduleN % v
      },
    mimaBinaryIssueFilters ++= {
      import com.typesafe.tools.mima.core._
      import com.typesafe.tools.mima.core.ProblemFilters._
      Seq()
    }
  )
}

lazy val skipOnPublishSettings = Seq(
  skip in publish := true,
  publish := (()),
  publishLocal := (()),
  publishArtifact := false,
  publishTo := None
)
