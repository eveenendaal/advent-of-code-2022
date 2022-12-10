import java.io.File

enum class Operation {
    NOOP,
    ADDX
}

data class Input(val operation: Operation, val value: Int?)

var register = 1
var cycle = 1
var signalStrength = 1

val input = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { input ->
        val parts = input.split(" ")
        val operation = Operation.values().first { it.name.equals(parts[0], true) }
        val value = if (parts.size > 1) {
            parts[1].toInt()
        } else null
        Input(operation, value)
    }

val pixels = (1..240).map { false }.toMutableList()
var signalHistory = listOf<Int>()
val inputs = input.iterator()
var nextAction: Input? = null
while (inputs.hasNext() || nextAction != null) {
    val input = if (inputs.hasNext() && nextAction == null) {
        inputs.next()
    } else {
        null
    }

    val before = register
    // Perform
    when (nextAction?.operation) {
        Operation.ADDX -> {
            register += nextAction!!.value!!
            nextAction = null
        }

        else -> {}
    }
    // Calculate Signal
    if ((cycle - 20) % 40 == 0) {
        signalStrength = cycle * before
        signalHistory += signalStrength
    }
    println("Cycle $cycle) action: $nextAction : $before -> $register (Input: $input, Signal Strength $signalStrength)")
    val drawPixel = (cycle - 1) % 40

    if (before == drawPixel) {
        pixels[cycle - 1] = true
    }
    if ((before - 1) == drawPixel && (drawPixel % 40) != 1) {
        pixels[cycle - 1] = true
    }
    if ((before + 1) == drawPixel && (drawPixel % 40) != 39) {
        pixels[cycle - 1] = true
    }
    printScren(pixels, cycle, drawPixel)

    // Check for input
    when (input?.operation) {
        Operation.ADDX -> nextAction = input
        else -> {}
    }

    cycle += 1
}

fun printScren(input: List<Boolean>, cycle: Int, drawPixel: Int) {
    var counter = 0

    println("Cycle $cycle, Draw Pixel $drawPixel")

    input.forEach {
        print(if (it) "#" else ".")
        counter += 1
        if (counter % 40 == 0) {
            println()
        }
    }
    println()
}

