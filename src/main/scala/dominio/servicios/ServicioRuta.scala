package dominio.servicios

import dominio.entidades.{Instruccion, Ruta}

import scala.util.Try

sealed trait ServicioRuta {

  def traerInstruccion(instruccion: String):Try[Instruccion]
  def traerRuta(linea:String):Ruta
  def traerListaInstruccines(linea:String):List[Instruccion]
  def pasarListaInstruccionesARuta(listaInstrucciones: List[Instruccion]): Ruta

}

sealed trait InterpreteServicioRuta extends ServicioRuta{

  def traerInstruccion(instruccion: String):Try[Instruccion]={
    Try{Instruccion.of(instruccion)}
  }

  def pasarListaInstruccionesARuta(listaInstrucciones: List[Instruccion]): Ruta = {
    Ruta(listaInstrucciones)
  }

  def traerListaInstruccines(linea: String): List[Instruccion] = {
    linea.toCharArray.toList
        .map(c=>c.toString)
        .map(s=>traerInstruccion(s))
        .filter(t=> t.isSuccess)
        .map(t=>t.get)
  }

  def traerRuta(linea: String): Ruta = {
      pasarListaInstruccionesARuta(traerListaInstruccines(linea))
  }
}

object InterpreteServicioRuta extends InterpreteServicioRuta



