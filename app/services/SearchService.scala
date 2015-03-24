package services

case class SearchResults(entries: List[SearchResultEntry])
case class SearchResultEntry(path: String)

object SearchService {
  def get(query: String): SearchResults = {
    SearchResults(List(
      SearchResultEntry("devsearch-epfl/devsearch-play/app/controllers/Application.scala"),
      SearchResultEntry("devsearch-epfl/devsearch-play/app/search/SearchService.scala")
    ))
  }
}
