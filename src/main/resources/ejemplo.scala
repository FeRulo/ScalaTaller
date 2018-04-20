
//Sustantivos
case class Dron(id: Int, capacidad: Int)
case class Almuerzo(ruta: Ruta)
case class Ruta(instrucciones: List[Instruccion])
trait Instruccion
object A extends Instruccion
object I extends Instruccion
object D extends Instruccion

trait ResultadoEntregaAlmuerzo
object AlmuerzoEntregado
object AlmuerzoFueraDeRango


//Verbos
trait ServicioEntregaAlmuerzos{
  def entregar(almuerzos: List[Almuerzo]): ResultadoEntregaAlmuerzo
}

trait InterpreteServicioEntregaAlmuerzos extends ServicioEntregaAlmuerzos{

  val listaDrones:List[Dron]
  def entregar(almuerzo: List[Almuerzo]): ResultadoEntregaAlmuerzo = {
    val drones = inicializarDrones()
    ???
  }

  private def inicializarDrones():List[Dron]={
    List(Dron(1,2), Dron(2,3))

  }

}

