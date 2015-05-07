package services

import akka.contrib.pattern.ClusterClient
import akka.pattern._
import akka.util.Timeout
import devsearch.lookup._
import play.libs.Akka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

object SearchService {

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")


  def get(request: SearchRequest, maxDuration: FiniteDuration = 10 seconds): Future[(SearchResult, Duration)] = {

    val startTime = System.nanoTime()

    implicit val timeout = new Timeout(maxDuration)

    val result = (clusterClient ? ClusterClient.Send("/user/lookup", request, localAffinity = true))
      .collect { case s: SearchResult => s }
      .recover {
      case e: AskTimeoutException => SearchResultError("Timeout.")
    }

    val timeTaken = result.map(_ => (System.nanoTime() - startTime) nanoseconds)

    for (res <- result; t <- timeTaken) yield (res, t)
  }
}
