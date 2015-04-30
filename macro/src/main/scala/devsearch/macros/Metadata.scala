package devsearch.macros

import scala.language.experimental.macros
import scala.reflect.macros.Context

/**
 * Created by dengels on 30/04/15.
 */
object Metadata {

  def supportedLanguages : Set[String] = macro languagesImpl

  def languagesImpl(c: Context) = {
    import c.universe._

    val parsersPackage = c.mirror.staticPackage("devsearch.parsers").typeSignature.members.filter {
       symbol => symbol.typeSignature <:< typeOf[devsearch.parsers.Parser]
    }

    c.Expr[Set[String]]{
      c.parse(parsersPackage.map(symbol => symbol.fullName + ".language" ).mkString("Set(", ",", ")"))
    }

  }
}
