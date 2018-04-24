package testArchivos

import java.io.FileNotFoundException

import dominio.servicios.{InterpreteAdministradorPedidos, InterpreteServicioRuta, tiempo}
import org.scalatest.FunSuite
import util.pool.global

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success

class MultiplesDronesSuite extends FunSuite{
  object servicioRuta extends InterpreteServicioRuta

 test("probando inicializar pedidos"){
    val listaArchivos = InterpreteAdministradorPedidos.inicializarArchivosEntrada()
    val pedidos = InterpreteAdministradorPedidos.inicializarPedidos(listaArchivos)
    val fl = pedidos.flatMap(lp=>InterpreteAdministradorPedidos.reportar(lp))
    assertResult((1 to 20).map(i=>"Escritura Exitosa").toList){
      Await.result(fl, 10 seconds)
    }
  }

  test("probar tiempo de inicializar Pedidos en paralelo"){
    val listaArchivos = InterpreteAdministradorPedidos.inicializarArchivosEntrada()
    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val pedidos = InterpreteAdministradorPedidos.inicializarPedidos(listaArchivos)
    Await.result(pedidos, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(elapsed >= estimatedElapsed)
  }

  test("probar tiempo de reportar en paralelo"){
    val listaArchivos = InterpreteAdministradorPedidos.inicializarArchivosEntrada()
    val pedidos = InterpreteAdministradorPedidos.inicializarPedidos(listaArchivos)

    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val fl = for{
      lp<-pedidos
    }yield(InterpreteAdministradorPedidos.reportar(lp))
    Await.result(fl, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(elapsed >= estimatedElapsed)
  }
}
