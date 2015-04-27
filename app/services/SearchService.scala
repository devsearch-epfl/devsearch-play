package services

import com.decodified.scalassh._
import devsearch.ast.ContentsSource
import scala.io.Source
import devsearch.parsers._
import devsearch.features._
import scala.util._
import scala.io.Source
import play.libs.Akka
import akka.contrib.pattern.ClusterClient
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await

case class SearchResults(entries: Seq[SearchResultEntry])
case class SearchResultEntry(repo: String, path: String, line: Int)

object SearchService {
  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")
  def get(query: String): SearchResults = {
    implicit val timeout = Timeout(Duration(1, "day")) //TODO change that...
    //val astQuery = List(QueryParser, JavaParser, GoParser).view.flatMap(p => Try(p.parse(query)).toOption).headOption
    val astQuery = List(JavaParser, GoParser, QueryParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println(query)
    println("astQuery is: " + astQuery) //always null

    astQuery match {
      case None => { //parser was not able to parse the snippet
        println("Unable to parse")
        SearchResults(Nil)
      }
      case Some(ast) => {
        println("Parsed")
        //give these features as input to the spark script to do look up

        val features = Features(
          CodeFileData(CodeFileLocation("dummy username", "dummy repo name", "dummy file name"), ast)
        ).map((f: Feature) => f.key).toList

        println("Features: " + features.size)
        //val stringified = features.map((f:Feature) => f.key)
        features.foreach(println)
        println("END Features" + features.size)

        val result = clusterClient ? ClusterClient.Send("/user/lookup", features, true)
        Await.result(result, timeout.duration) match {
          /*case Right(cmdResult: SearchResults) =>
            cmdResult*/
          case Right(result: Seq[(String, String, Long)]) =>
            SearchResults(result.map(r => SearchResultEntry(r._1, r._2, r._3.toInt)))
          case Left(error) => {
            println("error: " + error)
            SearchResults(Nil)
          }
        }
      }
    }
  }
}
