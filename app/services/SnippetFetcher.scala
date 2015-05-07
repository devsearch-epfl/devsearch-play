package services


import java.net.URLEncoder

import devsearch.lookup.SearchResultEntry
import models.SnippetResult
import play.api.Play.current
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by dengels on 21/04/15.
 */
object SnippetFetcher {

  def encodeGithubPath(path: String) = path.split('/').map(part => URLEncoder.encode(part, "UTF-8").replaceAll("\\+", "%20")).mkString("/")

  def getSnippetCode(entry: SearchResultEntry, size: Int): Future[SnippetResult] = {

    val encodedUser = encodeGithubPath(entry.user)
    val encodedRepo = encodeGithubPath(entry.repo)
    val encodedPath = encodeGithubPath(entry.path)
    val url = s"https://raw.githubusercontent.com/$encodedUser/$encodedRepo/master/$encodedPath"

    val code = WS.url(url).get().map { result =>
      result.body.lines.drop(entry.line - 1).take(size).mkString("\n")
    }

    code.map(Some(_)).recover{ case _ : Throwable => None } map { codeOpt =>
      SnippetResult(entry.user, entry.repo, entry.path, entry.line, entry.line + size, codeOpt)
    }
  }


}
