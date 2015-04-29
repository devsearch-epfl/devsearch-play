package controllers

import devsearch.lookup._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc._
import services.{SnippetFetcher, SearchService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def search(q: Option[String]) = Action.async {

    val timeout = 10 seconds

    val noResults = SearchResultSuccess(Seq.empty)

    val futureResults: Future[SearchResult] = q.map(query => SearchService.get(query, timeout)).getOrElse(Future.successful(noResults))

    val timedOut = akka.pattern.after(timeout, Akka.system.scheduler)(Future.successful(SearchResultError("Timeout")))

    val returned = Future.firstCompletedOf(Seq(futureResults, timedOut))

    val snippets: Future[Map[SearchResultEntry, String]] = returned.flatMap {
      case SearchResultSuccess(entries) =>

        /* Fetch all the snippets */
        val futureSnippets = entries.map(e => SnippetFetcher.getSnippetCode(e, 10).map(snip => (e, snip)))

        /* Keep only the successful ones */
        val successfulSnippets = futureSnippets.map(f => Future.sequence(List(f))).map(_.recover { case e: Throwable => Nil })

        Future.sequence(successfulSnippets).map(list => list.flatten.toMap)
      case e => Future.successful(Map.empty)
    }

    for (results <- returned; snips <- snippets) yield Ok(views.html.search(q, results, snips))
  }
}
