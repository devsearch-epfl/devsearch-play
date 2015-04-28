package controllers

import play.api._
import play.api.mvc._
import services._
import devsearch.lookup._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def search(q: Option[String]) = Action {
    val results: Option[SearchResult] = q.map(SearchService.get)
    Ok(views.html.search(q, results))
  }
}
