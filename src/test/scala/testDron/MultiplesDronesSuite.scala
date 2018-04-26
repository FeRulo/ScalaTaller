package testArchivos

import dominio.entidades._
import dominio.servicios.{InterpreteServicioDron, InterpreteServicioRuta}
import org.scalatest.FunSuite
import util.pool.global

import scala.concurrent.duration._
import scala.concurrent.Await

class MultiplesDronesSuite extends FunSuite{

 test("probando inicializar pedidos"){
    val listaArchivos = archivos.entrada
    val pedidos = InterpreteServicioDron.inicializarPedidos(listaArchivos)
    val fl = pedidos.flatMap(lp=>InterpreteServicioDron.reportarPedidos(lp))
    val res = Await.result(fl, 10 seconds)
    assertResult("(-3,5) dirección Norte\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-3,9) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-10,4) dirección Sur\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n" +
      "(-5,7) dirección Este\n"){
      res.foldLeft(""){(s,r)=>s"$s${InterpreteServicioDron.imprimirPosicion(r.dron.posicion)}\n"}
    }

  }

  test("probar tiempo en paralelo de inicializar Pedidos "){
    val listaArchivos = archivos.entrada
    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val pedidos = InterpreteServicioDron.inicializarPedidos(listaArchivos)
    Await.result(pedidos, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(Math.abs(elapsed - estimatedElapsed)<=0.1)
  }

  test("probar tiempo en paralelo de reportar o"){
    val listaArchivos = archivos.entrada
    val pedidos = InterpreteServicioDron.inicializarPedidos(listaArchivos)

    val estimatedElapsed:Double =  (tiempo.espera+10D)/1000

    val t1 = System.nanoTime()
    val fl = for{
      lp<-pedidos
    }yield(InterpreteServicioDron.reportarPedidos(lp))
    Await.result(fl, 10 seconds)
    val elapsed = (System.nanoTime() - t1) / 1.0E09

    println(s"tiempo de espera: estimado: $estimatedElapsed ,real: $elapsed")
    assert(Math.abs(elapsed - estimatedElapsed)<=0.1)
  }

}
