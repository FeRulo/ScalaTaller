package testArchivos

import org.scalatest.FunSuite
import dominio.entidades._
import dominio.servicios.InterpreteServicioRuta._
import dominio.servicios.InterpreteServicioArchivo._

import scala.io.Source
import scala.util.{Success, Try}

class ArchivoSuite extends FunSuite{

  test("crear lista de instrucciones"){
    val li = List(A(),D(),I())
    assert(List(A(), D(), I())==li)
  }

  test("instrucciones desde caracter"){
    val li = List(Instruccion.of("A"),Instruccion.of("D"),Instruccion.of("I"))
    assert(List(A(), D(), I())==li)
  }

  test("instrucciones desde caracterfallido"){
    val li = Try{List(Instruccion.of("*"),Instruccion.of("D"),Instruccion.of("I"))}
    assert(li.isFailure)
  }

  test("traer origen a Lista de String"){
    val origen = "src/main/resources/origen.txt"
    val lineas = Try{Source.fromFile(origen).getLines().toList}
    assertResult(Try{List("AAAAAIAD", "AAIDA", "AIAIAIAIA")}){
      lineas
    }
  }

  test("origen falso a Lista de String"){
    val origen = "src/main/resources/origenPopo.txt"
    val lineas = Try{Source.fromFile(origen).getLines().toList}
    assert(lineas.isFailure)
  }


  test("origen falso a Lista de String2"){
    val linea = Try{"AIADIA"}

  }
  test("convertir string instruccion a ruta"){
    val i = traerInstruccion("A")
    assertResult(Try{A()}){
      i
    }
  }

  test("convertir linea a ruta"){
    val linea = Try{"AID"}
    val ruta = traerRuta(linea)
    assertResult( Try{Ruta(List(A(),I(),D()))} ){
      ruta
    }
  }

  test("convertir lineaPopo a ruta"){
    val linea = Try{"***A**I**D*"}
    val ruta = traerRuta(linea)
    assertResult( Try{Ruta(List(A(),I(),D()))} ){
      ruta
    }
  }

  test("convertir origen a ruta"){
    val origen = "src/main/resources/origen.txt"
    val listaRutas = leerArchivo(origen)
    assertResult(Success(List(
      Ruta(List(A(), A(), A(), A(), A(), I(), A(), D())),
      Ruta(List(A(), A(), I(), D(), A())),
      Ruta(List(A(), I(), A(), I(), A(), I(), A(), I(), A()))))
    ){
      listaRutas
    }
  }

  test("convertir origenFalso a ruta"){
    val origen = "src/main/resources/origenFalso.txt"
    val listaRutas = leerArchivo(origen)
    assert(listaRutas.isFailure)
  }
  test("convertir origen malo a ruta"){
    val origen = "src/main/resources/origenMalo.txt"
    val listaRutas = leerArchivo(origen)
    assertResult(Success(List(
      Ruta(List(A(), I(), A())),
      Ruta(List(D(), I(), A())),
      Ruta(List(A(), A(), A(), I(), A())),
      Ruta(List()))))
    {
      listaRutas
    }
  }
}
