package dominio.servicios

import dominio.entidades.{Instruccion, Ruta}

import scala.util.Try

sealed trait ServicioRuta {

  def traerRuta(linea:Try[String]):Try[Ruta]
  def traerListaInstruccines(linea:Try[String]):Try[List[Instruccion]]
  def traerInstruccion(instruccion: String):Try[Instruccion]
  def pasarListaInstruccionesARuta(listaInstrucciones: Try[List[Instruccion]]): Try[Ruta]

}

sealed trait InterpreteServicioRuta extends ServicioRuta{
  def traerInstruccion(instruccion: String):Try[Instruccion]={
    Try{Instruccion.of(instruccion)}
  }

  def pasarListaInstruccionesARuta(listaInstrucciones: Try[List[Instruccion]]): Try[Ruta] = {
    listaInstrucciones.flatMap(li => Try{Ruta(li)})
  }

  def traerListaInstruccines(linea: Try[String]): Try[List[Instruccion]] = {
    linea.map(s=>s.toCharArray())
      .map(l=> l.toList
        .map(c=>c.toString)
        .map(s=>traerInstruccion(s))
        .filter(t=> t.isSuccess)
        .map(t=>t.get)
      )
  }

  def traerRuta(linea: Try[String]): Try[Ruta] = {
      pasarListaInstruccionesARuta(traerListaInstruccines(linea))
  }
}

object InterpreteServicioRuta extends InterpreteServicioRuta



