package services


import java.net.URLEncoder

import play.api.Logger
import play.api.libs.ws.WS

import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.combinator.RegexParsers

/**
 * Created by dengels on 21/04/15.
 */
object SnippetFetcher {

  def encodeGithubPath(path: String) = path.split('/').map(part => URLEncoder.encode(part, "UTF-8").replaceAll("\\+", "%20")).mkString("/")

  def getSnippetCode(repo: String, path: String, line: Int, size: Int): Future[String] = {

    val encodedRepo = encodeGithubPath(repo)
    val encodedPath = encodeGithubPath(path)
    val url = s"https://raw.githubusercontent.com/$encodedRepo/master/$encodedPath"

    WS.url(url).get().map { result => result.body.lines.drop(line-1).take(size).mkString("\n") }
  }


}
