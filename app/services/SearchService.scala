package services

import com.decodified.scalassh._
import devsearch.ast.ContentsSource
import play.api.cache.Cache
import scala.concurrent.Future
import scala.io.Source
import devsearch.parsers._
import devsearch.features._
import scala.util._
import scala.io.Source
import play.api.Play.current

case class SearchResults(entries: Seq[SearchResultEntry])
case class SearchResultEntry(repo: String, path: String, line: Int)

object SearchService {
  def get(query: String): Future[SearchResults] = Cache.getOrElse(query, 3600){
    //val astQuery = List(QueryParser, JavaParser, GoParser).view.flatMap(p => Try(p.parse(query)).toOption).headOption
    val astQuery = List(QueryParser, JavaParser, GoParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println(query)
    println("astQuery is: "+astQuery) //always null

    Future(
    astQuery match {
      case None => { //parser was not able to parse the snippet
        println("Unable to parse")
        SearchResults(Nil)
      }
      case Some(ast) => {
        println("Parsed")
        //give these features as input to the spark script to do look up

        val features = Features.apply(CodeFileData(CodeFileLocation("Username","RepoName","FileName"), ast)).map((f:Feature)=> f.key).toList //For a query, we don't care about the file location

        println("Features: " + features.size)
        //val stringified = features.map((f:Feature) => f.key)
        features.foreach(println)
        println("END Features" + features.size)

        val hostConfig = HostConfig(
          login=PublicKeyLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_KEY")),
          //login=PasswordLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_PASSWORD")),
          port=System.getenv("BIGDATA_PORT").toInt
        )
        val result = SSH(System.getenv("BIGDATA_HOST"), hostConfig) { client =>
          val command = "bash -ic \"spark-submit --master yarn-client --num-executors 25 " + System.getenv("BIGDATA_SPARK_JAR") + " " + features.mkString("\'", "\' \'", "\'") + "\""
          println("Executing: " + command)
          client.exec(command)
        }

        result match {
          case Right(cmdResult) => {
            val lines = Source.fromInputStream(cmdResult.stdOutStream).getLines.toSeq
            println(lines.mkString("\n"))
            SearchResults(lines.map(l => {
              val Array(repo, path, lineStr) = l.split(",")
              SearchResultEntry(repo, path, lineStr.toInt)
            }))
          }
          case Left(error) => {
            println("error: " + error)
            SearchResults(Nil)
          }
        }
      }
    })
  }
}
