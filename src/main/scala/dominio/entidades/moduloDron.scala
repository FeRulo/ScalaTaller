package dominio.entidades

sealed trait Direccion
object N extends Direccion
object S extends Direccion
object E extends Direccion
object O extends Direccion

case class Posicion(x:Int, y:Int, d: Direccion)
case class Dron(id:Int, posicion:Posicion, carga:Int)
case class Limite(radio:Int)
