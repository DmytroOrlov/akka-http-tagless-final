import Dependencies._

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.2")
addCompilerPlugin(("org.scalameta" % "paradise" % "3.0.0-M11").cross(CrossVersion.full))

lazy val circeVersion = "0.11.1"

lazy val zioVersion = "1.0-RC4"

lazy val `akka-http-tagless-final` = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.github.DmytroOrlov",
      scalaVersion := "2.12.8",
      version := "0.1.0-SNAPSHOT"
    )),
    scalacOptions ++= Seq(
      //      "-deprecation",
      "-Ypartial-unification"
    ),
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-zio" % zioVersion,
      "org.scalaz" %% "scalaz-zio-interop-cats" % zioVersion,
      "org.typelevel" %% "cats-core" % "2.0.0-M1",
      "org.typelevel" %% "cats-mtl-core" % "0.5.0",
      "org.typelevel" %% "cats-tagless-macros" % "0.7",
      "org.typelevel" %% "cats-effect" % "1.3.0",
      "io.monix" %% "monix" % "3.0.0-RC2",
      "com.typesafe.akka" %% "akka-http" % "10.1.8",
      "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      scalaTest % Test,
      scalaCheck % Test
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
