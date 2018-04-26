package dominio.servicios

import dominio.entidades._
import util.pool.global
import scala.concurrent.Future
import scala.util.Try
import scala.math.abs

sealed trait ServicioDron {
  def reportarPedidos(pedidos:List[Pedido]): Future[List[String]]
  def reportarRutas(listaRutas: List[Ruta], dron:Dron, limite: Limite): String
  def enviarDronPorRuta(dron:Dron, ruta:Ruta, limite:Limite): Either[String,Dron]
}

sealed trait InterpreteServicioDron extends ServicioDron {

  def reportarPedidos(pedidos:List[Pedido]): Future[List[String]] = {
    Future
      .sequence(
        pedidos
          .map(pedido=>(reportarPedido(pedido),InterpreteServicioArchivo.traerArchivoSalida(pedido.dron)))
          .map(tu=>tu._1
            .map(s=>
              InterpreteServicioArchivo.escribirReporteEnArchivo(tu._2,s)
            )
          )
      )
  }

  def reportarRutas(listaRutas: List[Ruta], dron: Dron, limite: Limite): String = {
    listaRutas
      .foldLeft(List(Either.cond(true,dron,""))){(led, ruta)=>
        led.head.isRight match{
          case true=>led.head.flatMap(d=>enviarDronPorRuta(d, ruta, limite))::led
          case false=>led
        }

      }
      .reverse.tail
      .foldLeft("==Reporte de entregas=="){ (reporte,edron) =>
        s"$reporte\n ${edron.fold(s => s, d => imprimirPosicion(d.posicion))}"
      }
  }

  def reportarPedido(pedido: Pedido):Future[String] ={
    Future{
      Thread.sleep(tiempo.espera)
      InterpreteServicioDron.reportarRutas(pedido.rutas,pedido.dron,Limite(10))
    }
  }

  def inicializarArchivo(archivo:String):Future[Try[List[Ruta]]] = {
    Future{
      Thread.sleep(tiempo.espera)
      InterpreteServicioArchivo.leerArchivo(archivo)
    }
  }

  def inicializarPedidos(listaArchivos: List[String]):Future[List[Pedido]] = {
    Future.sequence(
      listaArchivos
        .map(archivo=>inicializarArchivo(archivo))
    ).map(l=>l
      .zip(flotaDe.drones)
      .filter(tu=>tu._1.isSuccess)
      .map(tu=>(tu._1.get,tu._2))
      .map(tu=>Pedido(tu._1,tu._2))
    )
  }

  def enviarDronPorRuta(dron: Dron, ruta: Ruta, limite: Limite): Either[String, Dron] = {
    validarRuta(ruta, dron, limite).map(r =>{
      Dron(dron.id, verPosicionFinal(dron.posicion, r), dron.carga - 1)
    })
  }

  def validarRuta(ruta: Ruta, dron: Dron, limite: Limite): Either[String, Ruta] = {
    Right(ruta)
      .filterOrElse(r => dron.carga > 0, s"El dron no puede entregar más pedidos")
      .filterOrElse(r => validarPosicionFinalRuta(dron.posicion, r, limite),
        s"La ruta: ${InterpreteServicioRuta.imprimirRuta(ruta)} envía el drón fuera del límite")
  }

  def validarPosicionFinalRuta(posicion0: Posicion, ruta: Ruta, limite: Limite): Boolean = {
    val pf = verPosicionFinal(posicion0, ruta)
    abs(pf.x) <= limite.radio && abs(pf.y) <= limite.radio
  }

  def verPosicionFinal(posicion0: Posicion, ruta: Ruta): Posicion = {
    ruta.instrucciones.foldLeft(posicion0) { (p, i) => {
      i match {
        case A => adelantar(p)
        case I => girar(p,I)
        case D => girar(p,D)
      }
    }
    }
  }

  def adelantar(posicion: Posicion): Posicion = {
    posicion.d match {
      case N => Posicion(posicion.x, posicion.y + 1, posicion.d)
      case S => Posicion(posicion.x, posicion.y - 1, posicion.d)
      case E => Posicion(posicion.x + 1, posicion.y, posicion.d)
      case O => Posicion(posicion.x - 1, posicion.y, posicion.d)
    }
  }

  def girar(posicion: Posicion, instruccion: Instruccion): Posicion = {
    instruccion match {
      case I => posicion.d match {
        case N => Posicion(posicion.x, posicion.y, O)
        case S => Posicion(posicion.x, posicion.y, E)
        case E => Posicion(posicion.x, posicion.y, N)
        case O => Posicion(posicion.x, posicion.y, S)
      }
      case D => posicion.d match {
        case N => Posicion(posicion.x, posicion.y, E)
        case S => Posicion(posicion.x, posicion.y, O)
        case E => Posicion(posicion.x, posicion.y, S)
        case O => Posicion(posicion.x, posicion.y, N)
      }
      case _=> posicion
    }
  }

  def imprimirPosicion(posicion: Posicion): String = {
    s"(${posicion.x},${posicion.y}) dirección ${
      posicion.d match {
        case N => "Norte"
        case S => "Sur"
        case E => "Este"
        case O => "Oeste"
      }}"
  }

}

object InterpreteServicioDron extends InterpreteServicioDron