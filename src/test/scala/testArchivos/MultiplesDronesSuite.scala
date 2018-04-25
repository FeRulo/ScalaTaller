package testArchivos

import dominio.entidades.archivosDe
import dominio.servicios.{InterpreteDistribuidorPedidosSimultaneos,  InterpreteServicioRuta, tiempo}
import org.scalatest.FunSuite
import util.pool.global

import scala.concurrent.duration._
import scala.concurrent.{Await}

class MultiplesDronesSuite extends FunSuite{
  object servicioRuta extends InterpreteServicioRuta

 test("probando inicializar pedidos"){
    val listaArchivos = archivosDe.entrada
    val pedidos = InterpreteDistribuidorPedidosSimultaneos.inicializarPedidos(listaArchivos)
    val fl = pedidos.flatMap(lp=>InterpreteDistribuidorPedidosSimultaneos.reportar(lp))
    val res = Await.result(fl, 10 seconds)
    assertResult((1 to res.size).map(i=>"Escritura Exitosa").toList){
      res
    }
  }

  test("probar tiempo en paraleloo de inicializar Pedidos "){
    val listaArchivos = archivosDe.entrada
    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val pedidos = InterpreteDistribuidorPedidosSimultaneos.inicializarPedidos(listaArchivos)
    Await.result(pedidos, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(elapsed >= estimatedElapsed)
  }

  test("probar tiempo en paralelo de reportar o"){
    val listaArchivos = archivosDe.entrada
    val pedidos = InterpreteDistribuidorPedidosSimultaneos.inicializarPedidos(listaArchivos)

    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val fl = for{
      lp<-pedidos
    }yield(InterpreteDistribuidorPedidosSimultaneos.reportar(lp))
    Await.result(fl, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(elapsed >= estimatedElapsed)
  }
}
