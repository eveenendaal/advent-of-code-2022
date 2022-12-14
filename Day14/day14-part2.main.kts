import java.io.File

fun readInput(): Set<Pair<Int, Int>> {
    val rock = mutableSetOf<Pair<Int, Int>>()

    fun drawLine(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
        if (p1.first == p2.first) {
            val x = p1.first
            val ys = listOf(p1.second, p2.second)
            (ys.min()..ys.max()).forEach { y ->
                rock += Pair(x, y)
            }
        } else if (p1.second == p2.second) {
            val y = p1.second
            val xs = listOf(p1.first, p2.first)

            (xs.min()..xs.max()).forEach { x ->
                rock += Pair(x, y)
            }
        }
    }

    File("day14-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .forEach { line ->
            val points = line.split("->").map { it.trim() }
                .map {
                    val parts = it.split(",")
                    Pair(parts[0].toInt(), parts[1].toInt())
                }

            val pointIter = points.iterator()
            var current = pointIter.next()
            while (pointIter.hasNext()) {
                val next = pointIter.next()
                drawLine(next, current)
                current = next
            }
        }
    return rock
}

fun draw() {
    val everything = rock + sand + start + calcFloor()

    val minX = everything.minOf { it.first }
    val maxX = everything.maxOf { it.first }
    val minY = everything.minOf { it.second }
    val maxY = everything.maxOf { it.second }

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            val point = Pair(x, y)
            val result = if ((rock + calcFloor()).contains(point)) {
                "#"
            } else if (sand.contains(point)) {
                "o"
            } else if (start == point) {
                "+"
            } else {
                "."
            }
            print(result)
        }
        println()
    }
    println()
}

val start = Pair(500, 0)
val rock = readInput()
val sand = mutableSetOf<Pair<Int, Int>>()

enum class Action {
    LEFT,
    RIGHT,
    DOWN,
    STOP
}

var done = false

fun calcFloor(): Set<Pair<Int, Int>> {
    val everything = rock + sand + start

    val minX = everything.minOf { it.first } - 2
    val maxX = everything.maxOf { it.first } + 2
    val y = rock.maxOf { it.second } + 2

    return (minX..maxX).map { x ->
        Pair(x, y)
    }.toSet()
}

var blocks = rock + sand + calcFloor()

while (!done) {
    fun findBottom(point: Pair<Int, Int>): Pair<Int, Int> {
        var lastPoint = point
        var nextPoint = Pair(point.first, point.second + 1)

        while (!blocks.contains(nextPoint)) {
            lastPoint = nextPoint
            nextPoint = Pair(lastPoint.first, lastPoint.second + 1)
        }

        return lastPoint
    }

    fun findAction(point: Pair<Int, Int>): Pair<Action, Pair<Int, Int>> {
        val downleft = Pair(point.first - 1, point.second + 1)
        val downright = Pair(point.first + 1, point.second + 1)
        val bottom = Pair(point.first, point.second + 1)
        return if (!blocks.contains(bottom)) {
            Pair(Action.DOWN, findBottom(point))
        } else if (!blocks.contains(downleft)) {
            Pair(Action.LEFT, downleft)
        } else if (!blocks.contains(downright)) {
            Pair(Action.RIGHT, downright)
        } else {
            Pair(Action.STOP, point)
        }
    }

    var currentAction = findAction(start)
    while (currentAction.first != Action.STOP) {
        currentAction = findAction(currentAction.second)
    }

    if (currentAction.second.second > 0) {
        sand += currentAction.second
        blocks = rock + sand + calcFloor()
    } else {
        done = true
    }

    if (sand.size % 1000 == 0) {
        // draw()
        println(sand.size)
    }

}

//draw()
println("Round ${sand.size + 1}")
