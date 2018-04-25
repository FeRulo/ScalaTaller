package dominio.servicios

import dominio.entidades._
import dominio.servicios.InterpreteServicioArchivo.escribirReporteEnArchivo
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import util.pool.global

sealed trait AdministradorPedidos{
  def reportar(pedidos:List[Pedido]): Future[List[String]]
}
object tiempo{
  var espera = 1100
}

sealed trait InterpreteAdministradorPedidos extends AdministradorPedidos{

  override def reportar(pedidos:List[Pedido]): Future[List[String]] = {
    Future
      .sequence(
        pedidos
          .map(lp=>Future{lp})
          .map(f=>f.flatMap(p=>reportarUn(p)))
          .zip(archivos.salida)
          .map(tu=>tu._1.map(s=>
              escribirReporteEnArchivo(tu._2,s)
            )
          )
      )
  }

  def reportarUn(pedido:Pedido):Future[String] ={
    Thread.sleep(tiempo.espera)
    Future{InterpreteServicioDron.reportarRutas(Try{pedido.rutas},pedido.dron,Limite(10)).get}
  }

  def inicializarPedidos(listaArchivos: List[String]):Future[List[Pedido]] = {
    Future.sequence(listaArchivos
      .map(s=>Future{s})
      .map(f=>f
        .map(s=>{
          Thread.sleep(tiempo.espera)
          InterpreteServicioArchivo.leerArchivo(s)
        })
      )).map(l=>l
          .zip(flota.drones)
          .filter(tu=>tu._1.isSuccess)
          .map(tu=>(tu._1.get,tu._2))
          .map(tu=>Pedido(tu._1,tu._2))
        )
  }

}

object InterpreteAdministradorPedidos extends InterpreteAdministradorPedidos