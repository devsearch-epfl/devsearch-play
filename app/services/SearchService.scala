package services

import com.decodified.scalassh._
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
import scala.concurrent._

import devsearch.ast.ContentsSource
import devsearch.lookup._

//case class SearchResults(entries: Seq[SearchResultEntry])
//case class SearchResultEntry(repo: String, path: String, line: Int)

object SearchService {
  import play.api.libs.concurrent.Execution.Implicits._

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")
  def get(query: String): SearchResult = {
    val queryAst = List(JavaParser, GoParser, QueryParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println("--- new query ---")
    println("Input: " + query)

    queryAst match {
      case None => { //parser was not able to parse the snippet
        println("Unable to parse")
        SearchResultError("unable to parse")
      }
      case Some(ast) => {
        println("AST: " + ast)
        val features = Features(
          CodeFileData(CodeFileLocation("dummy username", "dummy repo name", "dummy file name"), ast)
        ).map((f: Feature) => f.key).toList

        println("Features: " + features.size)
        features.foreach(println)

        implicit val timeout = Timeout(Duration(30, "second"))
        val resultFuture = (
          clusterClient ? ClusterClient.Send("/user/lookup", SearchRequest(features), true)
        ).map {
          case r: SearchResult => r
        }
        val result: SearchResult = try {
          Await.result(resultFuture, timeout.duration)
        } catch {
          case e: TimeoutException => SearchResultError("Timeout")
        }
        result match {
          case SearchResultError(message) => println(s"error: $message")
          case SearchResultSuccess(entries) => println(s"received: ${entries.length} entries")
        }
        result
      }
    }
  }
}
