import play.PlayScala
import sbt._


object MyBuild extends Build {

  lazy val root = Project("root", file(".")).dependsOn(astParser).enablePlugins(PlayScala)
  lazy val astParser = RootProject(uri("git://github.com/devsearch-epfl/devsearch-ast.git"))

  /*
  object Github {
    def project(repo: String) = RootProject(uri(s"${repo}"))
    lazy val astParser = project("git://github.com/devsearch-epfl/devsearch-ast.git")
  }
  */

}
