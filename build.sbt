import Dependencies._

lazy val circeVersion = "0.12.1"

lazy val zioVersion = "1.0.0-RC14"

lazy val `akka-http-tagless-final` = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "io.github.DmytroOrlov",
      scalaVersion := "2.13.1",
      version := "0.1.0-SNAPSHOT"
    )),
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-deprecation",
    ),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC4",
      "org.typelevel" %% "cats-core" % "2.0.0",
      "org.typelevel" %% "cats-effect" % "2.0.0",
      "org.typelevel" %% "cats-mtl-core" % "0.7.0",
      "org.typelevel" %% "cats-tagless-macros" % "0.10",
      "io.monix" %% "monix" % "3.0.0",
      "com.typesafe.akka" %% "akka-http" % "10.1.10",
      "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      scalaTest % Test,
      scalaCheck % Test
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
  )
