package testArchivos

import java.io.FileNotFoundException

import org.scalatest.FunSuite
import dominio.entidades._
import dominio.servicios.InterpreteServicioRuta
import dominio.servicios.InterpreteServicioArchivo._
import dominio.servicios.InterpreteServicioDron._

import scala.io.Source
import scala.util.{Success, Try}

class ArchivoSuite extends FunSuite{
  object servicioRuta extends InterpreteServicioRuta

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
    val ruta = servicioRuta.traerRuta(linea)
    assertResult(Ruta(List(A,I,D))){
      ruta
    }
  }

  test("convertir linea Mala a ruta"){
    val linea = "***A**I**D*"
    val ruta = servicioRuta.traerRuta(linea)
    assertResult( Ruta(List(A,I,D)) ){
      ruta
    }
  }

  test("convertir origen a ruta"){
    val origen = "src/main/resources/in.txt"
    val listaRutas = leerArchivo(origen)
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
    val listaRutas = leerArchivo(origen)
    assert(listaRutas.isFailure)
  }

  test("convertir origen malo a ruta"){
    val origen = "src/main/resources/inMalo.txt"
    val listaRutas = leerArchivo(origen)
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
      verPosicionFinal(p,r)
    }
  }

  test("reportar Entregas"){
    val origen = "src/main/resources/in.txt"
    val listaRutas = leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1,Posicion(0,0,N),3)
    val destino = "src/main/resources/out.txt"
    val reporte = reportarRutas(listaRutas,dron,Limite(10))

    println(escribirReporteEnArchivo(destino,reporte))
    assertResult("==Reporte de entregas==\n" +
      " (-1,5) dirección Norte\n" +
      " (-1,8) dirección Norte\n" +
      " (-1,9) dirección Norte"){
      reporte
    }
  }

  test("reportar Entregas origen Falso"){
    val origen = "src/main/resources/inFalso.txt"
    val listaRutas = leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1,Posicion(0,0,N),3)
    val destino = "src/main/resources/outInFalso.txt"
    val reporte = reportarRutas(listaRutas,dron,Limite(10))
    println(escribirReporteEnArchivo(destino,reporte))
    assertResult("==Reporte de entregas==\n (0,0) dirección Norte"){
        reporte
    }
  }

  test("reportar Entregas origen Exceso Lineas") {
    val origen = "src/main/resources/inExcesoLineas.txt"
    val listaRutas = leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1, Posicion(0, 0, N), 3)
    val destino = "src/main/resources/outExcesoLineas.txt"
    val reporte = reportarRutas(listaRutas,dron,Limite(10))

    println(escribirReporteEnArchivo(destino,reporte))
    assertResult("==Reporte de entregas==\n " +
      "(0,2) dirección Este\n " +
      "(1,2) dirección Este\n" +
      " (2,4) dirección Este\n" +
      " El dron no puede entregar más pedidos\n" +
      " El dron no puede entregar más pedidos") {
      reporte
    }
  }

  test("reportar Entregas origen Saliendose Límites") {
    val origen = "src/main/resources/inSaliendoseLimites.txt"
    val listaRutas = leerArchivo(origen)
      .recover{case e: FileNotFoundException => {
        List(Ruta(List.empty[Instruccion]))
      }
      }.get
    val dron = Dron(1, Posicion(0, 0, N), 3)
    val destino = "src/main/resources/outSaliendoseLimites.txt"
    val reporte = reportarRutas(listaRutas,dron,Limite(10))

    println(escribirReporteEnArchivo(destino,reporte))
    assertResult("==Reporte de entregas==\n " +
      "(-1,6) dirección Oeste\n " +
      "La ruta envía el drón fuera del límite\n " +
      "La ruta envía el drón fuera del límite") {
      reporte
    }

  }
}