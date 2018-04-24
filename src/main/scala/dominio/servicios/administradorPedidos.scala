package dominio.servicios

import dominio.entidades.{Limite, Pedido}

import scala.util.Try

sealed trait AdministradorPedidos{
  def reportar(listaPedidos:List[Pedido],limite:Limite): List[String]
}

sealed trait InterpreteAdministradorPedidos extends AdministradorPedidos{
  override def reportar(listaPedidos: List[Pedido], limite: Limite): List[String] = ???
  def inicializarPedidos(): List[Pedido] = ???
  def inicializarArchivosEntrada(): List[String] = ???
}

object InterpreteAdministradorPedidos extends InterpreteAdministradorPedidos