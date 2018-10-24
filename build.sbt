ThisBuild / organization := "com.akkademy.db"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.7"

val akkaVersion = "2.5.17"
lazy val commonDependencies = Seq(
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val messages = project
  .settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )

lazy val server = project.dependsOn(messages)
  .settings(
    libraryDependencies ++= commonDependencies
  )

lazy val client = project.dependsOn(messages)
  .settings(
    libraryDependencies ++= commonDependencies
  )

lazy val newsaid = project.dependsOn(messages)
  .settings(
    libraryDependencies ++= commonDependencies,
    libraryDependencies += "com.syncthemall" % "boilerpipe" % "1.2.2"
  )

cancelable in Global := true