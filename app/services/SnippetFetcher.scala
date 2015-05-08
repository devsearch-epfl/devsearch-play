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

  def getSnippetCode(entry: SearchResultEntry): Future[SnippetResult] = {

    val encodedUser = encodeGithubPath(entry.user)
    val encodedRepo = encodeGithubPath(entry.repo)
    val encodedPath = encodeGithubPath(entry.path)
    val url = s"https://raw.githubusercontent.com/$encodedUser/$encodedRepo/master/$encodedPath"

    val size = entry.lineEnd - entry.lineStart + 1

    val code = WS.url(url).get().collect { case result if result.status == 200 =>
      result.body.lines.drop(entry.lineStart - 1).take(size).mkString("\n")
    }

    code.map(Some(_)).recover{ case _ : Throwable => None } map { codeOpt =>
      SnippetResult(entry.user, entry.repo, entry.path, entry.lineStart,
        entry.lineEnd, entry.scoreBreakDown, entry.featureList, codeOpt)
    }
  }


}
