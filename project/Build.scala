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
  lazy val astProjectCommit = "cdffdba2bc77e344eafa0252ccbd04d299933243"

  lazy val lookupProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-lookup.git#" + lookupProjectCommit))
  // uncoment the below for using local changes
  //lazy val lookupProject = RootProject(file("../devsearch-lookup"))
  lazy val lookupProjectCommit = "01c56ccad7bea8e70c9169e9a625ac2d6df3441b"

}
