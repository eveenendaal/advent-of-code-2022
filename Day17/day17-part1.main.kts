import java.io.File

enum class Movement {
    LEFT,
    RIGHT,
    UNKNOWN
}

fun parseMovements(): List<Movement> {
    return File("day17-test.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .flatMap { it.toList() }
        .map { it.toString() }
        .map {
            when (it) {
                ">" -> Movement.RIGHT
                "<" -> Movement.LEFT
                else -> Movement.UNKNOWN
            }
        }
}

data class Point(var x: Int, var y: Int)
data class Rock(var points: List<Point>, var active: Boolean = true)

var rocks = listOf<Rock>()

val width = 8

fun maxHeight(): Int {
    return rocks.filter { !it.active }.flatMap { it.points }.maxOfOrNull { it.y } ?: 0
}

fun makeRock(number: Int, startX: Int, startY: Int): Rock {
    val shapeNumber = (number - 1) / 5
    return when (shapeNumber % 5) {
        0 -> Rock(
            listOf(
                Point(startX + 0, startY + 0),
                Point(startX + 1, startY + 0),
                Point(startX + 2, startY + 0),
                Point(startX + 3, startY + 0)
            )
        )

        1 -> Rock(
            listOf(
                Point(startX + 1, startY + 0),
                Point(startX + 0, startY + 1),
                Point(startX + 1, startY + 1),
                Point(startX + 2, startY + 1),
                Point(startX + 1, startY + 2)
            )
        )

        2 -> Rock(
            listOf(
                Point(startX + 0, startY + 0),
                Point(startX + 1, startY + 0),
                Point(startX + 2, startY + 0),
                Point(startX + 2, startY + 1),
                Point(startX + 2, startY + 2)
            )
        )

        3 -> Rock(
            listOf(
                Point(startX + 0, startY + 0),
                Point(startX + 0, startY + 1),
                Point(startX + 0, startY + 2),
                Point(startX + 0, startY + 3)
            )
        )

        4 -> Rock(
            listOf(
                Point(startX + 0, startY + 0),
                Point(startX + 1, startY + 0),
                Point(startX + 0, startY + 1),
                Point(startX + 1, startY + 1)
            )
        )

        else -> throw RuntimeException()
    }
}

fun drawGrid(counter: Int) {
    val drawHeight = rocks.flatMap { it.points }.maxOfOrNull { it.y } ?: 0
    (0..drawHeight).reversed().forEach { y ->
        (0..width).forEach { x ->
            if (y == 0) {
                when (x) {
                    0, width -> print("+")
                    else -> print("-")
                }
            } else {
                when (x) {
                    0, width -> print("|")
                    else -> {
                        val point = rocks
                            .flatMap { rock -> rock.points.map { Pair(it, rock.active) } }
                            .firstOrNull { it.first.x == x && it.first.y == y }
                        if (point != null) {
                            if (point.second!!) {
                                print("@")
                            } else {
                                print("#")
                            }
                        } else {
                            print(".")
                        }
                    }
                }
            }
        }
        println()
    }
    println()
}

val movements = parseMovements()

fun moveRock(movement: Movement) {
    println("move $movement")

    rocks.filter { it.active }.forEach { rock ->
        val xs = rock.points.map { it.x }

        when (movement) {
            Movement.LEFT -> {
                if (xs.min() != 1) {
                    rock.points.forEach { it.x -= 1 }
                }
            }

            Movement.RIGHT -> {
                if (xs.max() != (width - 1)) {
                    rock.points.forEach { it.x += 1 }
                }
            }

            else -> {}
        }
    }
}

fun moveDown() {
    println("move down")

    rocks.filter { it.active }.flatMap { it.points }.forEach { it.y -= 1 }
}

var counter = 1
movements.forEach { movement ->
    if ((counter - 1) % 5 == 0) {
        val rock = makeRock(counter, 3, maxHeight() + 5)
        rock.active = true
        rocks += rock
    }

    rocks
        .filter { it.active }
        .forEach { rock ->
            // Hit Bottom
            if (rock.points.any { it.y - 1 == 0 }) {
                rock.active = false
                println("rock inactive")
            } else {
                val allPoints = rocks.filter { it != rock }.flatMap { it.points }
                rock.points.forEach {
                    val newPoint = it.copy(y = it.y - 1)
                    if (allPoints.contains(newPoint)) {
                        rock.active = false
                        println("rock inactive")
                    }
                }
            }
        }

    moveDown()
    moveRock(movement)
    drawGrid(counter)

    counter += 1
}



println(maxHeight())
