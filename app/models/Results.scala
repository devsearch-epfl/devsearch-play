package models

/**
 * Created by dengels on 07/05/15.
 */
case class SnippetResult(user: String, repo: String, path: String, startLine: Int, endLine : Int, code : Option[String]){
  def previewUrl = s"https://github.com/$user/$repo/blob/master/${path}L$startLine-L$endLine"
}

case class QueryInfo(query : String, detectedLang : Option[String], features : Set[String])