import Dependencies._

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
addCompilerPlugin(("org.scalameta" % "paradise" % "3.0.0-M11").cross(CrossVersion.full))

lazy val circeVersion = "0.11.1"

lazy val `akka-http-tagless-final` = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.github.DmytroOrlov",
      scalaVersion := "2.12.8",
      version := "0.1.0-SNAPSHOT"
    )),
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.6.0",
      "org.typelevel" %% "cats-mtl-core" % "0.4.0",
      "org.typelevel" %% "cats-tagless-macros" % "0.2.0",
      "org.typelevel" %% "cats-effect" % "1.2.0",
      "io.monix" %% "monix" % "3.0.0-RC2",
      "com.typesafe.akka" %% "akka-http" % "10.1.7",
      "de.heikoseeberger" %% "akka-http-circe" % "1.24.3",
      scalaTest % Test,
      scalaCheck % Test
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
