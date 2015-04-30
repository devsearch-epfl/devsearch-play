package services

import akka.contrib.pattern.ClusterClient
import akka.pattern._
import akka.util.Timeout
import devsearch.ast.ContentsSource
import devsearch.features._
import devsearch.lookup._
import devsearch.parsers._
import play.api.Logger
import play.libs.Akka

import scala.concurrent._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._


object SearchService {

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")

  def get(query: String, maxDuration: FiniteDuration): Future[SearchResult] = {
    val queryAst = List(JavaParser, GoParser, QueryParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    Logger.info("--- new query ---")
    Logger.info("Input: " + query)

    implicit val timeout = new Timeout(maxDuration)

    queryAst.map { ast =>

      Logger.info("AST: " + ast)

      val features = Features(
        CodeFileData(CodeFileLocation("dummy username", "dummy repo name", "dummy file name"), ast)
      ).map((f: Feature) => f.key).toList

      Logger.info("Features: " + features.size)
      features.zipWithIndex.foreach { case (feature, idx) => Logger.info(s" ${idx + 1}. $feature") }

      val results = (clusterClient ? ClusterClient.Send("/user/lookup", SearchRequest(features), localAffinity = true) collect { case s: SearchResult => s }).recover{
        case e: AskTimeoutException => SearchResultError("Timeout.")
      }

      results.onSuccess {
        case SearchResultError(message) => Logger.error(s"error: $message")
        case SearchResultSuccess(entries) => Logger.info(s"received: ${entries.length} entries")
      }
      results
    }.getOrElse {
      Logger.error("Unable to parse")
      Future.successful(SearchResultError("unable to parse"))
    }

  }

}
