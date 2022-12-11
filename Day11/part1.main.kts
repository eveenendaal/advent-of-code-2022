import java.io.File

data class Item(var worryLevel: Int)
data class Monkey(
    var number: Int,
    var items: List<Item>,
    val operation: (number: Int) -> Int,
    val action: (number: Int) -> Boolean,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspections: Int = 0,
)

fun parseInput(input: List<String>): List<Monkey> {
    val monkeys = emptyList<Monkey>().toMutableList()

    var currentMonkey: Int? = null
    var nextMonkeyTrue: Int? = null
    var nextMonkeyFalse: Int? = null
    var items: List<Item> = emptyList()
    var operation: ((Int) -> Int)? = null
    var test: ((Int) -> Boolean)? = null

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
                    .map { Item(it.toInt()) }
            } else if (line.startsWith("Operation:")) {
                // Operation
                val equation = line.substringAfter(": new =").trim().split(" ")
                val function = equation[1]
                val value = equation[2]

                operation = when (function) {
                    "*" -> {
                        if (value == "old") {
                            { input: Int -> input * input }
                        } else {
                            { input: Int -> input * value.toInt() }
                        }
                    }

                    "+" -> {
                        if (value == "old") {
                            { input: Int -> input + input }
                        } else {
                            { input: Int -> input + value.toInt() }
                        }
                    }

                    else -> throw RuntimeException("Unknown function")
                }

            } else if (line.startsWith("Test:")) {
                // Test
                val match = "[^0-9]+([0-9\\,\\ ]+)".toRegex().find(line)
                val value = match!!.groupValues[1].toInt()
                test = { input: Int -> input % value == 0 }

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

fun runRound() {
    monkeys.forEach { monkey ->
        monkey.items.forEach { item ->
            item.worryLevel = monkey.operation(item.worryLevel)
            item.worryLevel = item.worryLevel / 3
            monkey.inspections += 1
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

(1..20).forEach {
    runRound()
    println("Round $it")
    printMonkeys()
}

monkeys.forEach {
    println("Monkey ${it.number} -> ${it.inspections} Inspections")
}
val total = monkeys
    .sortedBy { it.inspections }
    .reversed()
    .subList(0, 2)
    .map { it.inspections }.reduce { a: Int, b: Int -> a * b }
println("Total $total")
