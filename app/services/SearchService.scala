package services

import akka.contrib.pattern.ClusterClient
import akka.pattern._
import akka.util.Timeout
import devsearch.lookup._
import play.libs.Akka
import play.api.Play.current

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps
import play.api.cache.Cache

object SearchService {

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")


  def get(request: SearchRequest, maxDuration: FiniteDuration = 20 seconds): Future[(SearchResult, Duration)] = Cache.getOrElse(request.toString, 86400){

    val startTime = System.nanoTime()

    implicit val timeout = new Timeout(maxDuration)

    val result = (clusterClient ? ClusterClient.Send("/user/lookup", request, localAffinity = true))
      .collect { case s: SearchResult => s }
      .recover {
      case e: AskTimeoutException => SearchResultError("Timeout. Could not get a response from the search back-end in time.")
    }

    val timeTaken = result.map(_ => (System.nanoTime() - startTime) nanoseconds)

    for (res <- result; t <- timeTaken) yield (res, t)
  }
}
