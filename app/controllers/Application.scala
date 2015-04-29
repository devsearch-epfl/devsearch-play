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
    val futureResults: Future[Option[SearchResult]] = q match {
      case Some(query) => SearchService.get(query, timeout).map(Some(_))
      case None => Future.successful(None)
    }

    val snippets: Future[Map[SearchResultEntry, String]] = futureResults.flatMap {
      case Some(SearchResultSuccess(entries)) =>

        /* Fetch all the snippets */
        val futureSnippets = entries.map(e => SnippetFetcher.getSnippetCode(e, 10).map(snip => (e, snip)))

        /* Keep only the successful ones */
        val successfulSnippets = futureSnippets.map(f => Future.sequence(List(f))).map(_.recover { case e: Throwable => Nil })

        Future.sequence(successfulSnippets).map(list => list.flatten.toMap)
      case _ => Future.successful(Map.empty)
    }

    for (results <- futureResults; snips <- snippets) yield Ok(views.html.search(q, results, snips))
  }
}
