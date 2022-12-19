import java.io.File

data class Valve(val name: String, val rate: Int, var next: Set<String> = emptySet())

fun Valve.nextValves(): Set<Valve> {
    return this.next.flatMap { nextName -> valves.values.filter { it.name == nextName } }.toSet()
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

val valves = emptyMap<String, Valve>().toMutableMap()

readInput().forEach {
    valves[it.name] = it
}

var currentValve = valves["AA"]!!

data class State(val timeLeft: Int, val pressureTotal: Int, val history: List<String>)

fun State.lastValve(): Valve {
    return valves[this.history.last()]!!
}

val queue = ArrayDeque<Pair<State, State>>()
queue.add(
    Pair(
        State(26, 0, listOf(currentValve.name)),
        State(26, 0, listOf(currentValve.name))
    )
)

val relevantValves = valves.values
    .filter { it.rate > 0 }
var bestScore = 0
var bestResult: Pair<State, State>? = null

var cachedCosts = emptyMap<String, Map<String, Int>>().toMutableMap()
cachedCosts[currentValve.name] = currentValve.calcCosts()
relevantValves.forEach {
    cachedCosts[it.name] = it.calcCosts()
}

queue.forEach { states ->
    val sortedStates = states.toList().sortedBy { it.timeLeft }.reversed()
    val state = sortedStates.first()

    val costs = cachedCosts[state.lastValve().name]!!
    val openedValves = (states.first.history + states.second.history).toSet()

    val remainingValves = relevantValves
        .filter { !openedValves.contains(it.name) }
        .filter { costs[it.name]!! <= state.timeLeft }
        .map { Pair(it, costs[it.name]!!) }

    if (remainingValves.isNotEmpty()) {
        remainingValves.forEach {
            val timeLeft = state.timeLeft - it.second
            val newState = State(
                timeLeft,
                (timeLeft * it.first.rate) + state.pressureTotal,
                state.history + it.first.name
            )
            queue.add(Pair(newState, sortedStates.last()))
        }
    } else {
        val total = states.first.pressureTotal + states.second.pressureTotal
        if (total > bestScore) {
            bestScore = total
            bestResult = states
            println(states)
            println(bestScore)
        }
    }
}
