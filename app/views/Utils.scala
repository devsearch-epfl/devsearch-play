package views

import devsearch.features._


object Utils {

  /**
   * Returns a nice string for each feature.
   */
  def toPrettyString(f: Feature): String = f match {
    case ClassNameFeature(position, name) =>
      "Class '"+ name +"'"
    case InheritanceFeature(position, className, superClassName) =>
      "'"+ className +"' inherits from '"+ superClassName +"'"
    case FieldFeature(position, name) =>
      "Field '"+ name +"'"
    case FunctionFieldFeature(position, name, args) =>
      "'"+ name +"' with Arguments '"+ args +"'"
    case FunNameFeature(position, name) =>
      "Function '"+ name +"'"
    case ArgNameFeature(position, name) =>
      "Argument '"+ name +"'"
    case ParametricFunFeature(position) =>
      "Parametric Function"
    case AbstractFunFeature(position) =>
      "Abstract Function"
    case OverridingFunFeature(position) =>
      "Overriding Function"
    case ThrowsFeature(position, exception) =>
      "Throws '"+ exception +"'"
    case ImportFeature(position, domain) =>
      "Import '"+ domain +"'"
    case MapCallFeature(position) =>
      "Map Call"
    case FlatMapCallFeature(position) =>
      "FlatMap Call"
    case ControlFeature(position, ctrl) =>
      "Control Statement '"+ ctrl +"'"
    case TypeRefFeature(position, path) =>
      "Type Reference '"+ path +"'"
    case TypedVarFeature(position, variableType, variableName) =>
      "Variable '"+ variableName + "' of Type '"+ variableType +"'"
    case VarFeature(position, name)  =>
      "Variable '"+ name +"'"
    case _ =>
      f.toString
  }

}
