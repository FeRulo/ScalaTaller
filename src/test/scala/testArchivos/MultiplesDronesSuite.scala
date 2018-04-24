package testArchivos

import java.io.FileNotFoundException

import dominio.entidades._
import dominio.servicios.InterpreteServicioArchivo._
import dominio.servicios.InterpreteServicioDron._
import dominio.servicios.{InterpreteAdministradorPedidos, InterpreteServicioRuta}
import org.scalatest.FunSuite
import util.pool.global

import scala.concurrent.Future
import scala.util.Success

class MultiplesDronesSuite extends FunSuite{
  object servicioRuta extends InterpreteServicioRuta

  test("Executor Context propio desde packaje object"){
    val f = Future{
      println(Thread.currentThread().getName)
      1
    }
  }

  test("probando inicializar pedidos"){
    val pedidos = InterpreteAdministradorPedidos.inicializarPedidos()
    assertResult((1 to 20).map(i=>"Escritura Exitosa").toList){
      InterpreteAdministradorPedidos.reportar(pedidos)
    }
  }
  test("probar tiempo en paralelo"){

    val estimatedElapsed = (500+50)/1000
    val t1 = System.nanoTime()
    val pedidos = InterpreteAdministradorPedidos.inicializarPedidos()
    InterpreteAdministradorPedidos.reportar(pedidos)
    val elapsed = (System.nanoTime() - t1) / 1.0E09
    println(s"futuros iniciador fuera del for-comp: estimado: $estimatedElapsed ,real: $elapsed")
    assert(elapsed >= estimatedElapsed)
  }
}
