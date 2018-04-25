package dominio.entidades

import scala.util.{Failure, Success, Try}

sealed trait Instruccion

object Instruccion {
  def of(i:String):Try[Instruccion] ={
    i match {
      case "A" => Success{A}
      case "D" => Success{D}
      case "I" => Success{I}
      case _ => Failure{new Exception(s"Caracter invalido para creacion de instruccion: $i")}
    }
  }
}

object A extends Instruccion
object D extends Instruccion
object I extends Instruccion


case class Ruta(instrucciones: List[Instruccion])
case class Pedido(rutas: List[Ruta], dron:Dron)

object archivos{val entrada:List[String]=(1 to 20).map(i=>f"src/main/resources2/in/in$i%02d.txt").toList}