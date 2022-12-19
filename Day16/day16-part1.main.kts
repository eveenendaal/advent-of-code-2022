import java.io.File

data class Valve(val name: String, val rate: Int, var next: Set<String> = emptySet(), var open: Boolean = false)

fun Valve.nextValves(): Set<Valve> {
    return this.next.flatMap { nextName -> valves.filter { it.name == nextName } }.toSet()
}

fun Valve.calcDistances(): Map<String, List<String>> {
    var result = emptyMap<String, List<String>>().toMutableMap()
    val queue = ArrayDeque<Pair<Valve, List<String>>>()
    this.nextValves().forEach { queue.add(Pair(it, emptyList())) }

    queue.forEach { next ->
        result[next.first.name] = next.second
        next.first.nextValves()
            .filter { this.name != it.name }
            .filter { !result.keys.contains(it.name) }
            .forEach {
                queue.add(Pair(it, next.second + next.first.name))
            }
    }

    return result
}

fun Valve.calcCosts(): Map<String, Int> {
    val distances = this.calcDistances()
    var result = emptyMap<String, Int>().toMutableMap()
    distances.keys.forEach {
        result[it] = distances[it]!!.size + 2
    }
    return result
}

fun readInput(): List<Valve> {
    val map = mutableMapOf<String, Set<String>>()
    var result = File("day16-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .map { line ->
            val groups = "^Valve ([A-Z]+) has flow rate=([0-9]+).*valves? ([A-Z\\,\\ ]+)$".toRegex().matchEntire(line)!!.groupValues
            val name = groups[1]
            val rate = groups[2].toInt()
            var valvesNames = groups[3].split(",").map { it.trim() }.toSet()
            Valve(name, rate, valvesNames)
        }

    return result
}

fun getValve(name: String): Valve {
    return this.valves.first { it.name == name }
}

val valves = readInput()
var currentValve = getValve("AA")
var timeRemaining = 30

data class State(val timeLeft: Int, val pressureTotal: Int, val history: List<String>, val notes: List<String>)

fun State.lastValve(): Valve {
    return getValve(this.history.last())
}

var finalStates = emptyList<State>()
val queue = ArrayDeque<State>()
queue.add(State(30, 0, listOf(currentValve.name), emptyList()))
queue.forEach { state ->
    val costs = state.lastValve().calcCosts()
    val remainingValves = valves
        .filter { it.rate > 0 }
        .filter { !state.history.contains(it.name) }
        .filter { costs[it.name]!! <= state.timeLeft }
        .map { Pair(it, costs[it.name]!!) }

    if (remainingValves.isNotEmpty()) {
        remainingValves.forEach {
            val timeLeft = state.timeLeft - it.second
            val newState = State(
                timeLeft,
                (timeLeft * it.first.rate) + state.pressureTotal,
                state.history + it.first.name,
                state.notes + "time left ${timeLeft}, rate ${it.first.rate}, ${it.first.name}"
            )
            queue.add(newState)
        }
    } else {
        finalStates += state
    }
}

finalStates
    .sortedBy { it.pressureTotal }
    .forEach { println(it) }
