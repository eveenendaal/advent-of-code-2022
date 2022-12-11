import java.io.File

data class Item(var worryLevel: Long)
data class Monkey(
    var number: Int,
    var items: List<Item>,
    val operation: (number: Long) -> Long,
    val action: (number: Long) -> Boolean,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspections: Long = 0,
)

var multipliers: Set<Int> = emptySet()

fun parseInput(input: List<String>): List<Monkey> {
    val monkeys = emptyList<Monkey>().toMutableList()

    var currentMonkey: Int? = null
    var nextMonkeyTrue: Int? = null
    var nextMonkeyFalse: Int? = null
    var items: List<Item> = emptyList()
    var operation: ((Long) -> Long)? = null
    var test: ((Long) -> Boolean)? = null

    input
        .map { it.trim() }
        .forEach { line ->
            if (line.startsWith("Monkey")) {
                // Monkey
                val match = "[^0-9]+([0-9]+)".toRegex().find(line)
                currentMonkey = match!!.groupValues[1].toInt()
            } else if (line.startsWith("Starting items:")) {
                // Items
                val match = "[^0-9]+([0-9\\,\\ ]+)".toRegex().find(line)
                items = match!!.groupValues[1].split(",")
                    .map { it.trim() }
                    .map { Item(it.toLong()) }
            } else if (line.startsWith("Operation:")) {
                // Operation
                val equation = line.substringAfter(": new =").trim().split(" ")
                val function = equation[1]
                val value = equation[2]

                operation = when (function) {
                    "*" -> {
                        if (value == "old") {
                            { input: Long -> input * input }
                        } else {
                            { input: Long -> input * value.toLong() }
                        }
                    }

                    "+" -> {
                        if (value == "old") {
                            { input: Long -> input + input }
                        } else {
                            { input: Long -> input + value.toLong() }
                        }
                    }

                    else -> throw RuntimeException("Unknown function")
                }

            } else if (line.startsWith("Test:")) {
                // Test
                val match = "[^0-9]+([0-9\\,\\ ]+)".toRegex().find(line)
                val value = match!!.groupValues[1].toInt()
                multipliers += value
                test = { input: Long -> input % value == 0L }

            } else if (line.startsWith("If true:")) {
                // True Monkey
                val match = "[^0-9]+([0-9]+)".toRegex().find(line)
                nextMonkeyTrue = match!!.groupValues[1].toInt()
            } else if (line.startsWith("If false:")) {
                // False Monkey
                val match = "[^0-9]+([0-9]+)".toRegex().find(line)
                nextMonkeyFalse = match!!.groupValues[1].toInt()
            } else {
                monkeys += Monkey(currentMonkey!!, items, operation!!, test!!, nextMonkeyTrue!!, nextMonkeyFalse!!)
            }
        }

    return monkeys
}

val input = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray().asList()

val monkeys = parseInput(input)

val lcm = multipliers.reduce { a: Int, b: Int -> a * b }
println(lcm)

fun runRound() {
    monkeys.forEach { monkey ->
        monkey.items.forEach { item ->
            item.worryLevel = monkey.operation(item.worryLevel)
            monkey.inspections += 1
            item.worryLevel = item.worryLevel % lcm
            val nextMonkeyNumber = if (monkey.action(item.worryLevel)) monkey.trueMonkey else monkey.falseMonkey
            val nextMonkey = monkeys.first { it.number == nextMonkeyNumber }
            nextMonkey.items += item
            monkey.items -= item
        }
    }
}

fun printMonkeys() {
    monkeys.forEach { monkey ->
        println("Monkey ${monkey.number} -> ${monkey.items}")
    }
    println()
}

fun printInspections() {
    monkeys.forEach {
        println("Monkey ${it.number} -> ${it.inspections} Inspections")
    }
    val total = monkeys
        .sortedBy { it.inspections }
        .reversed()
        .subList(0, 2)
        .map { it.inspections }.reduce { a: Long, b: Long -> a * b }
    println("Total $total")
}

(1..10000).forEach {
    runRound()
    if (it % 1000 == 0 || it == 1 || it == 20) {
        println("Round $it")
        printMonkeys()
        printInspections()
    }
}
