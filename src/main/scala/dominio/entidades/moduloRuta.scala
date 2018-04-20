package dominio.entidades

sealed trait Instruccion

object Instruccion {
  def of(c:String):Instruccion ={
    c match {
      case "A" => A
      case "D" => D
      case "I" => I
      case _ => throw new Exception(s"Caracter invalido para creacion de instruccion: $c")
    }
  }
}

object A extends Instruccion
object D extends Instruccion
object I extends Instruccion


case class Ruta(instrucciones:List[Instruccion])
