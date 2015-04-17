import play.PlayScala
import sbt._


object MyBuild extends Build {

  lazy val root = Project("root", file(".")).dependsOn(astParser).enablePlugins(PlayScala)
  lazy val astParser = RootProject(uri("git://github.com/devsearch-epfl/devsearch-ast.git#"+astCommit))
  //lazy val astParser = RootProject(file("../devsearch-ast"))
  lazy val astCommit = "f2940793f16c7f006d93c1d1dcf8a908ed21c65c"

  /*
  object Github {
    def project(repo: String) = RootProject(uri(s"${repo}"))
    lazy val astParser = project("git://github.com/devsearch-epfl/devsearch-ast.git")
  }
  */

}
