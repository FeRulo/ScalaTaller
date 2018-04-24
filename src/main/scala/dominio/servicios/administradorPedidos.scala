package dominio.servicios

import dominio.entidades._
import dominio.servicios.InterpreteServicioArchivo.escribirReporteEnArchivo
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import util.pool.global

sealed trait AdministradorPedidos{
  def reportar(pedidos:List[Pedido]): List[String]
}

sealed trait InterpreteAdministradorPedidos extends AdministradorPedidos{

  override def reportar(pedidos:List[Pedido]): List[String] = {
    val fl = Future
        .sequence(
          pedidos
            .map(lp=>Future{lp})
            .map(f=>f
              .map(p=>{
                Thread.sleep(500)
                print(s"trabajando dentro de hilo: ${Thread.currentThread().getName}\n")
                InterpreteServicioDron.reportarRutas(Try{p.rutas},p.dron,Limite(10)).get
              })
            )
            .zip(inicializarArchivosSalida)
            .map(tu=>tu._1.map(s=>
              escribirReporteEnArchivo(tu._2,s)
            ))
        )
    Await.result(fl, 10 seconds)
  }

  def inicializarPedidos(): List[Pedido] = {
    inicializarArchivosEntrada
      .map(s=>InterpreteServicioArchivo.leerArchivo(s))
      .zip(inicializarFlota)
      .filter(tu=>tu._1.isSuccess)
      .map(tu=>(tu._1.get,tu._2))
      .map(tu=>Pedido(tu._1,tu._2))
  }
  def inicializarArchivosEntrada(): List[String] = {
    (1 to 20).map(i=>f"src/main/resources2/in/in$i%02d.txt").toList
  }
  def inicializarArchivosSalida(): List[String] = {
    (1 to 20).map(i=>f"src/main/resources2/out/out$i%02d.txt").toList
  }
  def inicializarFlota(): List[Dron] = {
    (1 to 20).map(i=>Dron(i, Posicion(0, 0, N), 10)).toList
  }
}

object InterpreteAdministradorPedidos extends InterpreteAdministradorPedidos