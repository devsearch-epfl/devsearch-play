import play.PlayScala
import sbt._
import Keys._


object MyBuild extends Build {

  lazy val root = Project("root", file("."))
    .dependsOn(astProject)
    .dependsOn(lookupProject)
    .enablePlugins(PlayScala)

  lazy val astProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-ast.git#" + astProjectCommit))
  // uncoment the below for using local changes
  //lazy val astProject = RootProject(file("../devsearch-ast"))
  lazy val astProjectCommit = "cdffdba2bc77e344eafa0252ccbd04d299933243"

  lazy val lookupProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-lookup.git#" + lookupProjectCommit))
  // uncoment the below for using local changes
  //lazy val lookupProject = RootProject(file("../devsearch-lookup"))
  lazy val lookupProjectCommit = "9ef21eaa5bd42de755da24b9e7fe36abb10c6dc2"

}
