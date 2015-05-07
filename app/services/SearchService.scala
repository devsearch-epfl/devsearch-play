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

  def get(query: String, maxDuration: FiniteDuration): (Option[String], List[String], Future[SearchResult]) = {
    Logger.info("--- new query ---")
    Logger.info("Input: " + query)

    implicit val timeout = new Timeout(maxDuration)

    val (detectedLanguage, featureKeys) = QueryRecognizer(query) match {
      case Some(codeFile) =>
        Logger.info("Detected language for query: " + codeFile.language)
        (Some(codeFile.language), FeatureRecognizer(codeFile).map(_.key).toList)
      case _ => (None, List[String]())
    }
    Logger.info("Features: " + featureKeys.size)
    featureKeys.zipWithIndex.foreach { case (feature, idx) =>  Logger.info(s" ${idx + 1}. $feature") }
    val featureList: List[String] = featureKeys.zipWithIndex.map( x => (s"Feature ${x._2 + 1}: ${x._1}") )

    val results = (clusterClient ? ClusterClient.Send("/user/lookup", SearchRequest(featureKeys), localAffinity = true))
      .collect { case s: SearchResult => s }
      .recover {
      case e: AskTimeoutException => SearchResultError("Timeout.")
    }

    results.onSuccess {
      case SearchResultError(message) => Logger.error(s"error: $message")
      case SearchResultSuccess(entries) => Logger.info(s"received: ${entries.length} entries")
    }
    (detectedLanguage, featureList, results)
  }
}
