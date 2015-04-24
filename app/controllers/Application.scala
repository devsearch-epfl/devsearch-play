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
    val results = q.map(q => SearchService.get(q).map(_.entries)).getOrElse(Future(Seq.empty))

    val withCodeSnippets = results.flatMap(entries => Future.sequence(entries.map { entry =>
      SnippetFetcher.getSnippetCode(entry.repo, entry.path, entry.line, 10).map((_, entry))
    }))

    withCodeSnippets.map(res => Ok(views.html.search(q, res)))
  }
}