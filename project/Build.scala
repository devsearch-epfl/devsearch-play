import play.PlayScala
import sbt._


object MyBuild extends Build {

  lazy val root = Project("root", file("."))
    .dependsOn(astProject)
    .dependsOn(lookupProject)
    .enablePlugins(PlayScala)

  lazy val astProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-ast.git#" + astProjectCommit))
  // uncoment the below for using local changes
  //lazy val astProject = RootProject(file("../devsearch-ast"))
  lazy val astProjectCommit = "f2940793f16c7f006d93c1d1dcf8a908ed21c65c"

  lazy val lookupProject = RootProject(uri("git://github.com/devsearch-epfl/devsearch-lookup.git#" + lookupProjectCommit))
  // uncoment the below for using local changes
  //lazy val lookupProject = RootProject(file("../devsearch-lookup"))
  lazy val lookupProjectCommit = "65d45198192de0df0542000d77a2f5961d7d1a41"

}
