package dominio.servicios

import dominio.entidades.{A, D, I, Instruccion, Ruta}

import scala.util.Try

sealed trait ServicioRuta {

  def traerRuta(linea:String):Ruta
  def traerListaInstruccines(linea:String):List[Instruccion]
  def pasarListaInstruccionesARuta(listaInstrucciones: List[Instruccion]): Ruta

}

sealed trait InterpreteServicioRuta extends ServicioRuta{

  def pasarListaInstruccionesARuta(listaInstrucciones: List[Instruccion]): Ruta = {
    Ruta(listaInstrucciones)
  }

  def traerListaInstruccines(linea: String): List[Instruccion] = {
    linea.toCharArray.toList
        .map(c=>c.toString)
        .map(s=>Instruccion.of(s))
        .filter(t=> t.isSuccess)
        .map(t=>t.get)
  }

  def traerRuta(linea: String): Ruta = {
      pasarListaInstruccionesARuta(traerListaInstruccines(linea))
  }

  def imprimirRuta(ruta: Ruta):String = {
    ruta.instrucciones.foldLeft("")((s,i)=>s+imprimirInstruccion(i))
  }
  def imprimirInstruccion(instruccion: Instruccion):String={
    s"${instruccion match {
        case A => "A"
        case I => "I"
        case D => "D"
      }}"
  }
}

object InterpreteServicioRuta extends InterpreteServicioRuta



