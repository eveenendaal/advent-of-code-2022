import java.io.File
import kotlin.math.roundToInt

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

fun State.benefit(valve: String): Int {
    return if (this.timeLeft >= this.cost((valve))) {
        (this.timeLeft - this.cost(valve)) * valves[valve]!!.rate
    } else {
        0
    }
}

fun State.cost(valve: String): Int {
    val costs = cachedCosts[this.lastValve().name]!!
    return costs[valve]!!
}

queue.forEach { states ->
    val openedValves = (states.first.history + states.second.history).toSet()

    val remainingValves = relevantValves
        .filter { !openedValves.contains(it.name) }
        .filter { states.first.timeLeft >= states.first.cost(it.name) || states.second.timeLeft >= states.second.cost(it.name) }

    if (remainingValves.isNotEmpty()) {
        var benefits = states.toList()
            .flatMap { state -> remainingValves.map { state.benefit(it.name) } }
            .toSet()
            .sorted()
            .reversed()

        benefits = benefits.subList(0, listOf(1, (benefits.size.toDouble() / 3).roundToInt()).max())

        val sortedRemainingValves = remainingValves.filter {
            benefits.contains(states.first.benefit(it.name)) || benefits.contains(states.second.benefit(it.name))
        }

        sortedRemainingValves.forEach {
            queue.add(
                Pair(
                    State(
                        states.first.timeLeft - states.first.cost(it.name),
                        states.first.benefit(it.name) + states.first.pressureTotal,
                        states.first.history + it.name
                    ), states.second
                )
            )
            queue.add(
                Pair(
                    State(
                        states.second.timeLeft - states.second.cost(it.name),
                        states.second.benefit(it.name) + states.second.pressureTotal,
                        states.second.history + it.name
                    ), states.first
                )
            )
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
