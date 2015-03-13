name := """devsearch-play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

// Webjars

libraryDependencies ++= Seq(
  "org.webjars" % "materializecss" % "0.95.3",
  "org.webjars" % "jquery" % "2.1.3"
)