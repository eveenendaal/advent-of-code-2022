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

enum class Result(var score: Int) {
    Win(6),
    Lose(0),
    Draw(3)
}

data class Round(val action: Action, val reaction: Action?, val result: Result?)

fun calcResult(action: Action, reaction: Action): Result {
    return when (action) {
        Action.Rock -> {
            return when (reaction) {
                Action.Rock -> Result.Draw
                Action.Paper -> Result.Win
                Action.Scissors -> Result.Lose
            }
        }

        Action.Paper -> {
            return when (reaction) {
                Action.Rock -> Result.Lose
                Action.Paper -> Result.Draw
                Action.Scissors -> Result.Win
            }
        }

        Action.Scissors -> {
            return when (reaction) {
                Action.Rock -> Result.Win
                Action.Paper -> Result.Lose
                Action.Scissors -> Result.Draw
            }
        }
    }
}

fun calcAction(action: Action, result: Result): Action {
    return when (action) {
        Action.Rock -> {
            return when (result) {
                Result.Win -> Action.Paper
                Result.Lose -> Action.Scissors
                Result.Draw -> Action.Rock
            }
        }

        Action.Paper -> {
            return when (result) {
                Result.Win -> Action.Scissors
                Result.Lose -> Action.Rock
                Result.Draw -> Action.Paper
            }
        }

        Action.Scissors -> {
            return when (result) {
                Result.Win -> Action.Rock
                Result.Lose -> Action.Paper
                Result.Draw -> Action.Scissors
            }
        }
    }
}

var total = 0

inputStrings
    .filter { it.isNotEmpty() }
    .map {
        var row = it.split(" ")

        var action = when (row[0]) {
            "A" -> Action.Rock
            "B" -> Action.Paper
            "C" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        var reaction = when (row[1]) {
            "X" -> Action.Rock
            "Y" -> Action.Paper
            "Z" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        Round(action, reaction, null)
    }
    .forEach {
        total += it.reaction!!.score
        total += calcResult(it.action, it.reaction!!).score

        println(it)
        println(calcResult(it.action, it.reaction!!))
        println(total)
    }
