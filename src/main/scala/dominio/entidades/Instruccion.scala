package dominio.entidades

sealed trait Instruccion

object Instruccion {
  def of(c:String):Instruccion ={
    c match {
      case "A" => A()
      case "D" => D()
      case "I" => I()
      case _ => throw new Exception(s"Caracter invalido para creacion de instruccion: $c")
    }
  }
}

case class A() extends Instruccion
case class D() extends Instruccion
case class I() extends Instruccion
