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

data class Round(val action: Action, val reaction: Action, val result: Result)

fun calcResult(action: Action, reaction: Action): Result {
    when (action) {
        Action.Rock -> return when (reaction) {
            Action.Rock -> Result.Draw
            Action.Paper -> Result.Win
            Action.Scissors -> Result.Lose
        }

        Action.Paper -> return when (reaction) {
            Action.Rock -> Result.Lose
            Action.Paper -> Result.Draw
            Action.Scissors -> Result.Win
        }

        Action.Scissors -> return when (reaction) {
            Action.Rock -> Result.Win
            Action.Paper -> Result.Lose
            Action.Scissors -> Result.Draw
        }
    }
}

fun calcAction(action: Action, result: Result): Action {
    when (action) {
        Action.Rock -> return when (result) {
            Result.Win -> Action.Paper
            Result.Lose -> Action.Scissors
            Result.Draw -> Action.Rock
        }

        Action.Paper -> return when (result) {
            Result.Win -> Action.Scissors
            Result.Lose -> Action.Rock
            Result.Draw -> Action.Paper
        }

        Action.Scissors -> return when (result) {
            Result.Win -> Action.Rock
            Result.Lose -> Action.Paper
            Result.Draw -> Action.Scissors
        }
    }
}

var total = 0

inputStrings
    .filter { it.isNotEmpty() }
    .map {
        val row = it.split(" ")

        val action = when (row[0]) {
            "A" -> Action.Rock
            "B" -> Action.Paper
            "C" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        val reaction = when (row[1]) {
            "X" -> Action.Rock
            "Y" -> Action.Paper
            "Z" -> Action.Scissors
            else -> throw RuntimeException("Missing mapping")
        }
        val result = when (row[1]) {
            "X" -> Result.Lose
            "Y" -> Result.Draw
            "Z" -> Result.Win
            else -> throw RuntimeException("Missing mapping")
        }
        Round(action, reaction, result)
    }
    .forEach {
        val action = it.action
        val result = it.result
        val reaction = calcAction(action, result)

        total += reaction.score
        total += calcResult(action, reaction).score

        println("$action -> $reaction = $result")
        println(total)
    }
