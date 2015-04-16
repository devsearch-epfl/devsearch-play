package services

import com.decodified.scalassh._
import devsearch.ast.ContentsSource
import scala.io.Source
import devsearch.parsers._
import devsearch.features._
import scala.util._
import scala.io.Source

case class SearchResults(entries: Seq[SearchResultEntry])
case class SearchResultEntry(path: String)

object SearchService {
  def get(query: String): SearchResults = {

    //val astQuery = List(QueryParser, JavaParser, GoParser).view.flatMap(p => Try(p.parse(query)).toOption).headOption
    val astQuery = List(QueryParser, JavaParser, GoParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println(query)
    println("astQuery is: "+astQuery) //always null

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

        println("mkstring features: " + features.size + " " + features.mkString("\'", "\' \'", "\'"))
        val hostConfig = HostConfig(
          //login=PublicKeyLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_KEY")),
          //login=PasswordLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_PASSWORD")),
          port=22
        )
        val result = SSH("icdataportal2.epfl.ch", hostConfig) { client =>
          client.exec("sh devsearch.sh " + features.mkString("\'", "\' \'", "\'"))
        }

        result match {
          case Right(cmdResult) => {
            val lines = Source.fromInputStream(cmdResult.stdOutStream).getLines
            println(lines)
            SearchResults(lines.map(SearchResultEntry).toSeq)
          }
          case Left(error) => {
            println("error: " + error)
            SearchResults(Nil)
          }
        }
      }
    }
  }
}
