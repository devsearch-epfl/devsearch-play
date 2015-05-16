name := """devsearch-play"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies ++= Seq(
  "org.webjars" % "materializecss" % "0.95.3",
  "org.webjars" % "jquery" % "2.1.3",
  "com.decodified" %% "scala-ssh" % "0.7.0",
  "org.scalatest" %% "scalatest" % "2.1.7" % "test",
  "xalan" % "serializer" % "2.7.2",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.9",
  "org.webjars.bower" % "Chart.js" % "1.0.2"
)

// Less configuration

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

target in Compile in doc := baseDirectory.value / "api"
