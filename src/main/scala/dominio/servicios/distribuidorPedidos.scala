package dominio.servicios

import dominio.entidades._

import scala.util.Try
import scala.math.{abs}

sealed trait ServicioDron {
  def reportarRutas(listaRutas: Try[List[Ruta]], dron:Dron, limite: Limite): Try[String]
  def entregarPedido(dron:Either[String,Dron], ruta:Ruta, limite:Limite): Either[String,Dron]
}
sealed trait InterpreteServicioDron extends ServicioDron {

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
  def adelantar(posicion: Posicion): Posicion = {
    posicion.d match {
      case N => Posicion(posicion.x, posicion.y + 1, posicion.d)
      case S => Posicion(posicion.x, posicion.y - 1, posicion.d)
      case E => Posicion(posicion.x + 1, posicion.y, posicion.d)
      case O => Posicion(posicion.x - 1, posicion.y, posicion.d)
    }
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
  def validarPosicionFinalRuta(posicion0: Posicion, ruta: Ruta, limite: Limite): Boolean = {
    val pf = verPosicionFinal(posicion0, ruta)
    abs(pf.x) <= limite.radio && abs(pf.y) <= limite.radio
  }
  def validarRuta(ruta: Ruta, dron: Dron, limite: Limite): Either[String, Ruta] = {
    Right(ruta)
      .filterOrElse(r => dron.carga > 0, s"El dron no puede entregar más pedidos")
      .filterOrElse(r => validarPosicionFinalRuta(dron.posicion, r, limite),
        s"La ruta envía el drón fuera del límite")
  }
  def entregarPedido(dron: Either[String, Dron], ruta: Ruta, limite: Limite): Either[String, Dron] = {
    dron.flatMap(d =>
      validarRuta(ruta, d, limite).map(r =>{
        Dron(d.id, verPosicionFinal(d.posicion, r), d.carga - 1)
      })
    )
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

  def reportarRutas(listaRutas: Try[List[Ruta]], dron: Dron, limite: Limite): Try[String] = {
    listaRutas
      .map(lr => lr
        .foldLeft(List(Either.cond(true,dron,""))){(led, ruta)=>
          (entregarPedido(led.head, ruta, limite))::led
        }
        .reverse.tail
        .foldLeft("==Reporte de entregas=="){ (reporte,edron) =>
          s"$reporte\n ${edron.fold(s => s, d => imprimirPosicion(d.posicion))}"
        }
      )
  }
}

object InterpreteServicioDron extends InterpreteServicioDron