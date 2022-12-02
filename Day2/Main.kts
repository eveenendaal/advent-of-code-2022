import java.io.File

// Read Input
val inputStrings = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()

enum class Action(var score: Int) {
    Rock(1),
    Paper(2),
    Scissors(3)
}

data class Row(val input: Action, val output: Action)

fun win(row: Row): Int {
    return when (row.input) {
        Action.Rock -> {
            return when (row.output) {
                Action.Rock -> 3
                Action.Paper -> 6
                Action.Scissors -> 0
            }
        }

        Action.Paper -> {
            return when (row.output) {
                Action.Rock -> 0
                Action.Paper -> 3
                Action.Scissors -> 6
            }
        }

        Action.Scissors -> {
            return when (row.output) {
                Action.Rock -> 6
                Action.Paper -> 0
                Action.Scissors -> 3
            }
        }
    }
}

var total = 0

inputStrings
    .filter { it.isNotEmpty() }
    .map {
        var row = it.split(" ")

        var input = when (row[0]) {
            "A" -> Action.Rock
            "B" -> Action.Paper
            "C" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        var output = when (row[1]) {
            "X" -> Action.Rock
            "Y" -> Action.Paper
            "Z" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        Row(input, output)
    }
    .forEach {
        total += it.output.score
        total += win(it)

        println(it)
        println(win(it))
        println(total)
    }
