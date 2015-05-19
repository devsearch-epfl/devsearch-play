package views

import devsearch.features._


object Utils {

  /** Number of results to put in each page */
  val NB_RESULTS_IN_PAGE = 10


  def lastPage(nbResults: Long): Int = ((nbResults - 1) / NB_RESULTS_IN_PAGE + 1).toInt

  /**
   * Returns a nice string for each feature.
   */
  def toPrettyString(f: Feature, withLine : Boolean = false): String = {
    val res = f match {
      case ClassNameFeature(position, name) => s"Class '$name'"
      case InheritanceFeature(position, className, superClassName) => s"'$className' inherits from '$superClassName'"
      case FieldFeature(position, name) => s"Field '$name'"
      case FunctionFieldFeature(position, name, args) => s"Function call '$name' with $args Arguments"
      case FunNameFeature(position, name) => s"Function '$name'"
      case ArgNameFeature(position, name) => s"Argument '$name'"
      case ParametricFunFeature(position) => "Parametric function"
      case AbstractFunFeature(position) => "Abstract function"
      case OverridingFunFeature(position) => "Overriding function"
      case ThrowsFeature(position, exception) => s"Throws '$exception'"
      case ImportFeature(position, domain) => s"Import '$domain'"
      case MapCallFeature(position) => "Map call"
      case FlatMapCallFeature(position) => "FlatMap call"
      case ControlFeature(position, ctrl) => s"Control flow statement '$ctrl'"
      case TypeRefFeature(position, path) => s"Type reference '$path'"
      case TypedVarFeature(position, variableType, variableName) => s"Variable '$variableName' of type '$variableType'"
      case VarFeature(position, name) => s"Variable '$name'"
      case _ =>
        f.toString
    }

    if(withLine) s"$res at line ${f.pos.line}" else res
  }

  /**
   * Hack to reconstruct correct feature object to pretty print it nicely
   */
  val CNF = """className=([^\s]+)""".r
  val InF = """inheritence=([^\s]+) from=([^\s]+)""".r
  val FF = """fieldName=([^\s]+)""".r
  val FFF = """functionFieldName=([^\s]+) args=([^\s]+)""".r
  val FNF = """functionName=([^\s]+)""".r
  val AN = """argumentName=([^\s]+)""".r
  val PFF = """function is parametric"""
  val AFF = """abstractFunction"""
  val OFF = """overridingFunction"""
  val TF = """throwsException=([^\s]+)""".r
  val ImF = """import=([^\s]+)""".r
  val MCF = """map call"""
  val FMCF = """flatMap call"""
  val CF = """controlStatement=([^\s]+)""".r
  val TRF = """typeReference=([^\s]+)""".r
  val TVF = """variableDeclaration=([^\s]+) type=([^\s]+)""".r
  val VF = """variableName=([^\s]+)""".r

  def hackGuessFeatureObject(f : Feature) : Feature = f.key match {
      case CNF(name) => ClassNameFeature(f.pos, name)
      case InF(cls, parent) => InheritanceFeature(f.pos, cls, parent)
      case FF(name) => FieldFeature(f.pos, name)
      case FFF(name, args) => FunctionFieldFeature(f.pos, name, args.toInt)
      case FNF(name) => FunNameFeature(f.pos, name)
      case AN(name) => ArgNameFeature(f.pos, name)
      case PFF => ParametricFunFeature(f.pos)
      case AFF => AbstractFunFeature(f.pos)
      case OFF => OverridingFunFeature(f.pos)
      case TF(exception) => ThrowsFeature(f.pos, exception)
      case ImF(path) => ImportFeature(f.pos, path)
      case MCF => MapCallFeature(f.pos)
      case FMCF => FlatMapCallFeature(f.pos)
      case CF(ctrl) => ControlFeature(f.pos, ctrl)
      case TRF(typ) => TypeRefFeature(f.pos, typ)
      case TVF(name, typ) => TypedVarFeature(f.pos, typ, name)
      case VF(name) => VarFeature(f.pos, name)
      case _ => f
  }

}
