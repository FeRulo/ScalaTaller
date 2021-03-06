package testArchivos

import java.io.FileNotFoundException

import org.scalatest.FunSuite
import dominio.entidades._
import dominio.servicios.InterpreteServicioRuta
import dominio.servicios.InterpreteServicioArchivo
import dominio.servicios.InterpreteServicioDron

import scala.io.Source
import scala.util.{Success, Try}

class DronSuite extends FunSuite{

  test("crear lista de instrucciones"){
    val li = List(A,D,I)
    assert(List(A, D, I)==li)
  }

  test("instrucciones desde caracter"){
    val li = List(Instruccion.of("A"),Instruccion.of("D"),Instruccion.of("I"))
    assert(List(Success{A}, Success{D}, Success{I})==li)
  }

  test("instrucciones desde caracterfallido"){
    val li = List(Instruccion.of("*"),Instruccion.of("D"),Instruccion.of("I"))
    assert(li.head.isFailure)
  }

  test("convertir linea a ruta"){
    val linea = "AID"
    val ruta = InterpreteServicioRuta.traerRuta(linea)
    assertResult(Ruta(List(A,I,D))){
      ruta
    }
  }

  test("convertir linea Mala a ruta"){
    val linea = "***A**I**D*"
    val ruta = InterpreteServicioRuta.traerRuta(linea)
    assertResult( Ruta(List(A,I,D)) ){
      ruta
    }
  }

  test("convertir origen a ruta"){
    val origen = "src/main/resources/in.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
    assertResult(Success(List(
      Ruta(List(A, A, A, A, A, I, A, D)),
      Ruta(List(A, A, I, D, A)),
      Ruta(List(A, I, A, I, A, I, A, I, A))
    ))){
      listaRutas
    }
  }

  test("convertir origenFalso a ruta"){
    val origen = "src/main/resources/origenFalso.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
    assert(listaRutas.isFailure)
  }

  test("convertir origen malo a ruta"){
    val origen = "src/main/resources/inMalo.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
    assertResult(Success(List(
      Ruta(List(A, I, A)),
      Ruta(List(D, I, A)),
      Ruta(List(A, A, A, I, A)),
      Ruta(List()))))
    {
      listaRutas
    }
  }
  test("cambiando posición"){
    val p = Posicion(0,0,N)
    val r = Ruta(List(A,A,A,A,I,A,A,D))
    assertResult(Posicion(-2,4,N)){
      InterpreteServicioDron.verPosicionFinal(p,r)
    }
  }

  test("reportar Entregas"){
    val origen = "src/main/resources/in.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1,Posicion(0,0,N),3)
    val destino = "src/main/resources/out.txt"
    val reporte = InterpreteServicioDron.reportarRutas(listaRutas,dron,Limite(10))
    assertResult("==Reporte de entregas==\n" +
      " (-1,5) dirección Norte\n" +
      " (-1,8) dirección Norte\n" +
      " (-1,9) dirección Norte"){
      InterpreteServicioArchivo.escribirReporteEnArchivo(destino,reporte.historial)
    }
  }

  test("reportar Entregas origen Falso"){
    val origen = "src/main/resources/inFalso.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1,Posicion(0,0,N),3)
    val destino = "src/main/resources/outInFalso.txt"
    val reporte = InterpreteServicioDron.reportarRutas(listaRutas,dron,Limite(10))
    assertResult("==Reporte de entregas==\n (0,0) dirección Norte"){
      InterpreteServicioArchivo.escribirReporteEnArchivo(destino,reporte.historial)
    }
  }

  test("reportar Entregas origen Exceso Lineas") {
    val origen = "src/main/resources/inExcesoLineas.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1, Posicion(0, 0, N), 3)
    val destino = "src/main/resources/outExcesoLineas.txt"
    val reporte = InterpreteServicioDron.reportarRutas(listaRutas,dron,Limite(10))
    assertResult("==Reporte de entregas==\n " +
      "(0,2) dirección Este\n " +
      "(1,2) dirección Este\n" +
      " (2,4) dirección Este\n" +
      " El dron no puede entregar más pedidos") {
      InterpreteServicioArchivo.escribirReporteEnArchivo(destino,reporte.historial)
    }
  }

  test("reportar Entregas origen Saliendose Límites, el dron debe quedar en la última posición válida") {
    val origen = "src/main/resources/inSaliendoseLimites.txt"
    val listaRutas = InterpreteServicioArchivo.leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1, Posicion(0, 0, N), 3)
    val destino = "src/main/resources/outSaliendoseLimites.txt"
    val reporte = InterpreteServicioDron.reportarRutas(listaRutas,dron,Limite(10))
    assertResult("==Reporte de entregas==\n " +
      "(-1,6) dirección Oeste\n " +
      "La ruta: DAAAAAA envía el drón fuera del límite" +
      "(-1,6) dirección Oeste") {
      s"${InterpreteServicioArchivo.escribirReporteEnArchivo(destino,reporte.historial)}${InterpreteServicioDron.imprimirPosicion(reporte.dron.posicion)}"
    }

  }
}