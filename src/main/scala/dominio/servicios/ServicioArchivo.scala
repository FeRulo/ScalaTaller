package dominio.servicios

import dominio.entidades.Ruta
import dominio.servicios.InterpreteServicioRuta._

import scala.io.Source
import scala.util.Try

sealed trait ServicioArchivo {
  def leerArchivo(origen: String):Try[List[Ruta]]
  def escribirArchivo(destino: String, lineas:List[String]): Try[List[String]]
}

sealed trait InterpreteServicioArchivo extends ServicioArchivo{

  override def leerArchivo(origen: String): Try[List[Ruta]] = {
    val lineas = Try{Source.fromFile(origen).getLines().toList}
    for{
      lista<-lineas
    }yield(
      lista.map(s=>traerRuta(s))
    )
  }


  override def escribirArchivo(destino: String, lineas: List[String]): Try[List[String]] = ???
}

object InterpreteServicioArchivo extends InterpreteServicioArchivo
