ThisBuild / organization := "com.akkademy.db"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.6"

val akkaVersion = "2.5.16"

lazy val messages = project

lazy val server = project.dependsOn(messages)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-agent" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val client = project.dependsOn(messages)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-agent" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )
