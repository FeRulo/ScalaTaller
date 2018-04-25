package dominio.servicios

import dominio.entidades.{Instruccion, Ruta}

import scala.util.Try

sealed trait ServicioRuta {

  def traerRuta(linea:String):Ruta
  def traerListaInstruccines(linea:String):List[Instruccion]
  def pasarListaInstruccionesARuta(listaInstrucciones: List[Instruccion]): Ruta

}

trait InterpreteServicioRuta extends ServicioRuta{

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
}

object InterpreteServicioRuta extends InterpreteServicioRuta


