import play.PlayScala
import sbt._
import Keys._


object MyBuild extends Build {

  lazy val root = Project("root", file("."))
    .dependsOn(astProject)
    .dependsOn(lookupProject)
    .dependsOn(macroSub)
    .enablePlugins(PlayScala)

  lazy val macroSub = Project("macro", file("macro")).settings(
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)
    ).dependsOn(astProject)


  lazy val astProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-ast.git#" + astProjectCommit))
  // uncoment the below for using local changes
  //lazy val astProject = RootProject(file("../devsearch-ast"))
  lazy val astProjectCommit = "bec55a248de3ecd79dc543e9a865a92075a861b5"

  lazy val lookupProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-lookup.git#" + lookupProjectCommit))
  // uncoment the below for using local changes
  //lazy val lookupProject = RootProject(file("../devsearch-lookup"))
  lazy val lookupProjectCommit = "5f814a964c1f6ecb196a72b5e6d6f6a5adc9159b"

}
