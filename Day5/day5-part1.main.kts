import java.io.File

data class Stack(var number: Int, var crates: List<Crate> = emptyList())
data class Crate(var letter: String)

var stacks: List<Stack> = emptyList()

val inputRanges = File("day5-state.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .filter { it.contains("[") }
    .map { line ->
        var columnNumber = 1
        (0..line.length)
            .filter { it % 4 == 0 }
            .forEach {
                val stack = stacks.firstOrNull { stack -> stack.number == columnNumber } ?: Stack(columnNumber)
                if (!stacks.contains(stack)) {
                    stacks += stack
                }

                val crateString = line.substring(it, it + 3)
                if (crateString.isNotBlank()) {
                    stack.crates += Crate(crateString)
                }
                columnNumber += 1
            }
    }

val inputRanges = File("day5-input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach { line ->
        val match = "[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)".toRegex().matchEntire(line)
        if (match != null) {
            val moveCount = match.groupValues[1].toInt()
            val startColumnNumber = match.groupValues[2].toInt()
            val endColumnNumber = match.groupValues[3].toInt()

            val startColumn = stacks.first { it.number == startColumnNumber }
            val endColumn = stacks.first { it.number == endColumnNumber }

            repeat((1..moveCount).count()) {
                val crate = startColumn.crates.first()
                startColumn.crates -= crate
                endColumn.crates = listOf(crate) + endColumn.crates
            }
        }
    }

println(stacks)

stacks.forEach {
    print(it.crates.first().letter.replace("[^A-Z]".toRegex(), ""))
}
