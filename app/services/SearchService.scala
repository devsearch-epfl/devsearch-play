package services

import devsearch.parsers._
import devsearch.features._
import scala.util._
import play.libs.Akka
import akka.contrib.pattern.ClusterClient
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent._

import devsearch.ast.ContentsSource
import devsearch.lookup._

//case class SearchResults(entries: Seq[SearchResultEntry])
//case class SearchResultEntry(repo: String, path: String, line: Int)

object SearchService {
  import play.api.libs.concurrent.Execution.Implicits._

  val initialContacts = Set(Akka.system.actorSelection("akka.tcp://lookupCluster@127.0.0.1:2555/user/receptionist"))
  val clusterClient = Akka.system.actorOf(ClusterClient.props(initialContacts), "clusterClient")
  def get(query: String): SearchResult = {
    val queryAst = List(JavaParser, GoParser, QueryParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println("--- new query ---")
    println("Input: " + query)

    queryAst match {
      case None => { //parser was not able to parse the snippet
        println("Unable to parse")
        SearchResultError("unable to parse")
      }
      case Some(ast) => {
        println("AST: " + ast)
        val features = Features(
          CodeFileData(CodeFileLocation("dummy username", "dummy repo name", "dummy file name"), ast)
        ).map((f: Feature) => f.key).toList

        println("Features: " + features.size)
        features.foreach(println)

        implicit val timeout = Timeout(Duration(30, "second"))
        val resultFuture = (
          clusterClient ? ClusterClient.Send("/user/lookup", SearchRequest(features), true)
        ).map {
          case r: SearchResult => r
        }
        val result: SearchResult = try {
          Await.result(resultFuture, timeout.duration)
        } catch {
          case e: TimeoutException => SearchResultError("Timeout.")
        }
        result match {
          case SearchResultError(message) => println(s"error: $message")
          case SearchResultSuccess(entries) => println(s"received: ${entries.length} entries")
        }
        result
      }
    }
/*
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

case class SearchResults(entries: Seq[SearchResultEntry])

case class SearchResultEntry(repo: String, path: String, line: Int)

object SearchService {
  def get(query: String): Future[SearchResults] = {
    //val astQuery = List(QueryParser, JavaParser, GoParser).view.flatMap(p => Try(p.parse(query)).toOption).headOption
    val astQuery = List(QueryParser, JavaParser, GoParser).map(p => Try(p.parse(new ContentsSource("query", query)))) collectFirst { case Success(ast) => ast }

    println(query)
    println("astQuery is: " + astQuery) //always null

    Future(
      astQuery match {
        case None => {
          //parser was not able to parse the snippet
          println("Unable to parse")
          SearchResults(Nil)
        }
        case Some(ast) => {
          println("Parsed")
          //give these features as input to the spark script to do look up

          val features = Features.apply(CodeFileData(CodeFileLocation("Username", "RepoName", "FileName"), ast)).map((f: Feature) => f.key).toList //For a query, we don't care about the file location

          println("Features: " + features.size)
          //val stringified = features.map((f:Feature) => f.key)
          features.foreach(println)
          println("END Features" + features.size)

          val hostConfig = HostConfig(
            login = PublicKeyLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_KEY")),
            //login=PasswordLogin(System.getenv("BIGDATA_USER"), System.getenv("BIGDATA_PASSWORD")),
            port = System.getenv("BIGDATA_PORT").toInt
          )
          val result = SSH(System.getenv("BIGDATA_HOST"), hostConfig) { client =>
            val command = "bash -ic \"spark-submit --master yarn-client --num-executors 25 " + System.getenv("BIGDATA_SPARK_JAR") + " " + features.mkString("\'", "\' \'", "\'") + "\""
            println("Executing: " + command)
            client.exec(command)
          }

          result match {
            case Right(cmdResult) => {
              val lines = Source.fromInputStream(cmdResult.stdOutStream).getLines.toSeq
              println(lines.mkString("\n"))
              SearchResults(lines.map(l => {
                val Array(repo, path, lineStr) = l.split(",")
                SearchResultEntry(repo, path, lineStr.toInt)
              }))
            }
            case Left(error) => {
              println("error: " + error)
              SearchResults(Nil)
            }
          }
        }
      })
*/
  }
}
