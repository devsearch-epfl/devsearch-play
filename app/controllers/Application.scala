package controllers

import devsearch.features.{FeatureRecognizer, QueryRecognizer}
import devsearch.lookup._
import devsearch.parsers.Languages
import models.{QueryInfo, SnippetResult}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import play.api.mvc._
import services.{SearchService, SnippetFetcher}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.experimental.macros
import scala.language.postfixOps


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  /* Extract language selectors from the form */
  val languageFormatter = new Formatter[Set[String]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Set[String]] = {
      val res = if (key != "languages") Set.empty[String]
      else data.filter {
        case (k, v) => Languages.isLanguageSupported(k) && v == "on"
      }.keySet

      Right(res)
    }

    override def unbind(key: String, value: Set[String]): Map[String, String] = {
      if (key != "languages") Map.empty else value.map(_ -> "on").toMap
    }
  }

  case class SearchQuery(query: Option[String], langSelectors: Set[String])

  val EmptySearch = SearchQuery(None, Set.empty)
  val searchForm = Form(
    mapping(
      "query" -> optional(text),
      "languages" -> of(languageFormatter)
    )(SearchQuery.apply)(SearchQuery.unapply)
  )


  def search = Action.async { implicit req =>

    val search = searchForm.bindFromRequest.get

    search.query map { query =>


      val queryInfo = QueryRecognizer(query) map { codeFile =>
        QueryInfo(query, Some(codeFile.language), FeatureRecognizer(codeFile).map(_.key))
      } getOrElse {
        QueryInfo(query, None, Set.empty)
      }

      val futureResults = SearchService.get(SearchRequest(queryInfo.features.toSeq))

      /** Either result or error message */
      val futureSnippets: Future[(Either[Seq[SnippetResult], String], Duration)] = futureResults.flatMap {
        case (results, time) =>

          val snippets = results match {
            case SearchResultSuccess(entries) =>
              val withSnippets = entries.map { e => SnippetFetcher.getSnippetCode(e, 10) }
              Future.sequence(withSnippets).map(Left(_))

            case SearchResultError(msg) => Future.successful(Right(msg))
          }

          snippets map ((_, time))
      }

      for ((results, timeTaken) <- futureSnippets) yield Ok(views.html.search(search, queryInfo, results, timeTaken))


    } getOrElse {
      Future.successful(Ok(views.html.search(EmptySearch, QueryInfo("", None, Set.empty), Left(Seq.empty), Duration.Zero)))
    }

  }
}
