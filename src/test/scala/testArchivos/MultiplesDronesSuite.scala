package testArchivos

import java.io.FileNotFoundException

import dominio.entidades._
import dominio.servicios.InterpreteServicioArchivo._
import dominio.servicios.InterpreteServicioDron._
import dominio.servicios.InterpreteServicioRuta
import org.scalatest.FunSuite

import scala.util.Success

class MultiplesDronesSuite extends FunSuite{
  object servicioRuta extends InterpreteServicioRuta

  test("reportar Entregas origen Saliendose Límites") {
    val origen = "src/main/resources/inSaliendoseLimites.txt"
    val listaRutas = leerArchivo(origen)
    val dron = Dron(1, Posicion(0, 0, N), 3)
    val destino = "src/main/resources/outSaliendoseLimites.txt"
    val reporte = reportarRutas(listaRutas,dron,Limite(10))
      .recover{case e: FileNotFoundException => {
        "ORIGEN NO EXISTENTE DEL ARCHIVO DE ENTRADA"
      }
      }.get
    println(escribirReporteEnArchivo(destino,reporte))
    assertResult("==Reporte de entregas==\n " +
      "(-1,6) dirección Oeste\n " +
      "La ruta envía el drón fuera del límite\n " +
      "La ruta envía el drón fuera del límite") {
      reporte
    }

  }
}
