package controllers

import play.api._
import play.api.mvc._
import services._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def search(q: Option[String]) = Action.async {
    val results: Option[SearchResults] = q.map(SearchService.get)

    val seq: Seq[SearchResultEntry] = results.map {
      _.entries
    }.toSeq.flatten

    val withCodeSnippets = Future.sequence(seq.map { entry =>
      SnippetFetcher.getSnippetCode(entry.repo, entry.path, entry.line, 10).map((_, entry))
    })

    withCodeSnippets.map(res => Ok(views.html.search(q, res)))
  }
}