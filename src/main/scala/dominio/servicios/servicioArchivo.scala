package dominio.servicios

import java.io.{File, PrintWriter}

import dominio.entidades.{Dron, Pedido, Ruta}
import dominio.servicios.InterpreteServicioRuta._

import scala.io.Source
import scala.util.Try

sealed trait ServicioArchivo {
  def leerArchivo(origen: String):Try[List[Ruta]]
  def escribirReporteEnArchivo(destino: String, lineas:String): String
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

  override def escribirReporteEnArchivo(destino: String, lineas:String): String = {
    val pw = new PrintWriter(new File(destino ))
    pw.write(lineas)
    pw.close
    s"Escritura Exitosa"
  }

  def traerArchivoSalida(dron:Dron): String={
    f"src/main/resources2/out/out${dron.id}%02d.txt"
  }
}

object InterpreteServicioArchivo extends InterpreteServicioArchivo
