package controllers

import devsearch.macros.Metadata
import devsearch.lookup._
import play.api.Logger
import play.api.Play.current
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.libs.concurrent.Akka
import play.api.mvc._
import services.{SnippetFetcher, SearchService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.language.experimental.macros


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  /* All the languages we can parse */
  val languages = devsearch.macros.Metadata.supportedLanguages

  /* Extract language selectors from the form */
  val languageFormatter = new Formatter[Set[String]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Set[String]] = {
      val res = if(key != "languages") Set.empty[String] else data.filter{
        case (k, v) =>  languages.contains(k) && v == "on"
      }.keySet

      Right(res)
    }
    override def unbind(key: String, value: Set[String]): Map[String, String] = {
      if(key !="languages") Map.empty else value.map(_ -> "on").toMap
    }
  }

  case class SearchQuery(query: Option[String], langSelectors: Set[String])
  val searchForm = Form(
    mapping(
      "query" -> optional(text),
      "languages" -> of(languageFormatter)
    )(SearchQuery.apply)(SearchQuery.unapply)
  )

  def search = Action.async { implicit req =>

    val search = searchForm.bindFromRequest.get

    val timeout = 10 seconds
    val futureResults: Future[Option[SearchResult]] = search.query match {
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

    for (results <- futureResults; snips <- snippets) yield Ok(views.html.search(search, results, snips, languages))
  }
}
