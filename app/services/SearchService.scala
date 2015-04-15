package services

import com.decodified.scalassh._
import scala.io.Source
import devsearch.parsers._
import devsearch.features._
import scala.util._


case class SearchResults(entries: Seq[SearchResultEntry])
case class SearchResultEntry(path: String)

object SearchService {
  def get(query: String): SearchResults = {
    //val hostConfig = PasswordLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_PASSWORD"))

    //val astQuery = List(QueryParser, JavaParser, GoParser).view.flatMap(p => Try(p.parse(query)).toOption).headOption
    val astQuery = List(QueryParser, JavaParser, GoParser).map(p => Try(p.parse(query))) collectFirst { case Success(ast) => ast } getOrElse(null)

    println("astQuery is: "+astQuery) //always null

    astQuery match {
      case null => {
        println("Unable to parse")
        SearchResults(Nil)
        //parser was not able to parse the snippet
      }
      case _ => {
        println("Parsed")
        val features = Features.apply(CodeFileData(CodeFileLocation("null","null","null"), astQuery)) //Make it as Hubi did o/w unmatch...
        //give these features as input to the spark script to do look up

        val hostConfig = PublicKeyLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_KEY"))
        val result = SSH("icdataportal2.epfl.ch", hostConfig) { client =>
          client.exec("sh devsearch.sh")
        }

        result match {
          case Right(cmdResult) => {
            val lines = Source.fromInputStream(cmdResult.stdOutStream).getLines
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