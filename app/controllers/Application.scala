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

    val seq : Option[Seq[SearchResultEntry]] = results.map{_.entries}

    val withCodeSnippets = seq.map {
      _.foldLeft(Future(Seq.empty[(String, SearchResultEntry)])){ (future, entry) =>
          future.flatMap{
            seq => SnippetFetcher.getSnippetCode(entry.repo, entry.path, entry.line).map(snippet => seq :+ (snippet, entry))
          }
      }
    }

    /** swap option and future */
    val enhancedResults = withCodeSnippets match {
      case None => Future(None)
      case Some(t) => t.map(Some(_))
    }

    enhancedResults.map(res => Ok(views.html.search(q, res)))

  }
}