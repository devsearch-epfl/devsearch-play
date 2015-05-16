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
  lazy val astProjectCommit = "7d87ff69ef2b15e96f720117c6c17137506d2ab1"

  lazy val lookupProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-lookup.git#" + lookupProjectCommit))
  // uncoment the below for using local changes
  //lazy val lookupProject = RootProject(file("../devsearch-lookup"))
  lazy val lookupProjectCommit = "01c56ccad7bea8e70c9169e9a625ac2d6df3441b"

}
