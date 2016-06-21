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

  val CacheExpiration = (1 day).toSeconds.toInt

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")


  def get(request: SearchRequest, maxDuration: FiniteDuration = 20 seconds): Future[(SearchResult, Duration)] = {

    // Requests are cached depending on the list of features not the exact code
    val cacheKey = request.toString

    // check the cache first
    val resOpt = Cache.getAs[(SearchResult, Duration)](cacheKey)
    resOpt.map(Future(_)).getOrElse {

      val startTime = System.nanoTime()

      implicit val timeout = new Timeout(maxDuration)

      // This is "lazy" it will only be computed when used
      def nanoTime = (System.nanoTime() - startTime) nanoseconds

      val lookupResults = (clusterClient ? ClusterClient.Send("/user/lookup", request, localAffinity = true))

      lookupResults.collect {
        case s: SearchResultSuccess =>
          val res = (s, nanoTime)

          // We cache successfull requests
          Cache.set(cacheKey, res, CacheExpiration)
          res

        case s: SearchResultError =>
          // failed requests are not cached
          (s, nanoTime)

      } recover {
        case e: AskTimeoutException =>
          val error = SearchResultError("Timeout. Could not get a response from the search back-end in time.")
          (error, nanoTime)
      }
    }
  }
}
