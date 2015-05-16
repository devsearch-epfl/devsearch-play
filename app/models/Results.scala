package models

import devsearch.parsers.Languages
import devsearch.features.Feature

/**
 * Created by dengels on 07/05/15.
 */
case class SnippetResult(user: String, repo: String, path: String, startLine: Int, endLine : Int, scoreBreakDown : Map[String, Double], featureList : Set[String], code : Option[String]){

  def previewUrl = s"https://github.com/$user/$repo/blob/master/${path}#L$startLine-L$endLine"

  def repoUrl = s"https://github.com/$user/$repo"

  def language = Languages.guess(path).map(_.toLowerCase)

  def truncadedPath = if (path.length > 50){
   path.lastIndexOf("/") match {
     case -1 => path
     case i =>
       val (prefix, filename) = path.splitAt(i)
       val shorter = prefix.take(50 - filename.length).reverse.dropWhile(_ != '/').reverse
       shorter + "..." + filename
   }
  } else {
    path
  }

  def extendedStartLine = math.max(0, startLine - 3)
  def extendedEndLine = endLine + 3
}

case class QueryInfo(query : String, detectedLang : Option[String], features : Set[Feature])
