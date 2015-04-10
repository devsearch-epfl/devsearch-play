package services

import com.decodified.scalassh._
import scala.io.Source

case class SearchResults(entries: Seq[SearchResultEntry])
case class SearchResultEntry(path: String)

object SearchService {
  def get(query: String): SearchResults = {
    //val hostConfig = PasswordLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_PASSWORD"))
    /*TODO
    Parse the query with Nicolas V. parser, extract features, start a spark job that lookups on the HDFS and find the best matches.
     */
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