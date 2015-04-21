package services


import play.api.libs.ws.WS

import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by dengels on 21/04/15.
 */
object SnippetFetcher {

  def getSnippetCode(repo : String, path : String, line : Int) : Future[String] = {

     val url =  s"https://raw.githubusercontent.com/$repo/master/$path"

    WS.url(url).get().map { result =>
      result.body.lines.drop(line - 1).take(10).mkString("\n")
    }
  }


}
