package testArchivos

import dominio.entidades.{archivos, tiempo}
import dominio.servicios.{InterpreteServicioDron}
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
    assertResult((1 to res.size).map(i=>"Escritura Exitosa").toList){
      res
    }
  }

  test("probar tiempo en paraleloo de inicializar Pedidos "){
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
