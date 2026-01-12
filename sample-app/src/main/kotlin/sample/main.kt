package sample

import paug.january.runtime.DependencyGraph
import paug.january.runtime.Inject

@DependencyGraph
interface PaugGraph {
  val pizza: Pizza
}

@Inject
class Mozzarella {
  override fun toString() = "de l'excellente mozzarella"
}

@Inject
class Pizza(val mozzarella: Mozzarella) {
  override fun toString() = "Une bonne pizza avec $mozzarella \uD83C\uDF55"
}

fun main() {
  val graph: PaugGraph = PaugGraph.Impl()

  println(graph.pizza)
}