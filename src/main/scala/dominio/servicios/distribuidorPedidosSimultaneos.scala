package dominio.servicios

import dominio.entidades._
import dominio.servicios.InterpreteServicioArchivo.{escribirReporteEnArchivo,traerArchivoSalida}
import scala.concurrent.{Future}
import scala.util.Try
import util.pool.global

sealed trait DistribuidorPedidosSimultaneos{
  def reportar(pedidos:List[Pedido]): Future[List[String]]
}
object tiempo{
  var espera = 1100
}

sealed trait InterpreteDistribuidorPedidosSimultaneos extends DistribuidorPedidosSimultaneos{

  override def reportar(pedidos:List[Pedido]): Future[List[String]] = {
    Future
      .sequence(
        pedidos
          .map(pedido=>(reportarUn(pedido),traerArchivoSalida(pedido.dron)))
          .map(tu=>tu._1
            .map(s=>
              escribirReporteEnArchivo(tu._2,s)
            )
          )
      )
  }

  def reportarUn(pedido: Pedido):Future[String] ={
    Future{
      Thread.sleep(tiempo.espera)
      InterpreteServicioDron.reportarRutas(Try{pedido.rutas},pedido.dron,Limite(10)).get
    }
  }

  def inicializarUn(archivo:String):Future[Try[List[Ruta]]] = {
    Future{
      Thread.sleep(tiempo.espera)
      InterpreteServicioArchivo.leerArchivo(archivo)
    }
  }

  def inicializarPedidos(listaArchivos: List[String]):Future[List[Pedido]] = {
    Future.sequence(
        listaArchivos
          .map(archivo=>inicializarUn(archivo))
      ).map(l=>l
          .zip(flotaDe.drones)
          .filter(tu=>tu._1.isSuccess)
          .map(tu=>(tu._1.get,tu._2))
          .map(tu=>Pedido(tu._1,tu._2))
        )
  }

}

object InterpreteDistribuidorPedidosSimultaneos extends InterpreteDistribuidorPedidosSimultaneos