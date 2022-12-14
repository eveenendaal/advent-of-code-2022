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
        .forEach {
            val points = it.split("->").map { it.trim() }
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
    val everything = rock + sand + start

    val minX = everything.map { it.first }.min()
    val maxX = everything.map { it.first }.max()
    val minY = everything.map { it.second }.min()
    val maxY = everything.map { it.second }.max()

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            val point = Pair(x, y)
            val result = if (rock.contains(point)) {
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

val bottomOfCave = rock.map { it.second }.max()

fun outBottom(point: Pair<Int, Int>): Boolean {
    return (point.second > bottomOfCave)
}

var done = false

while (!done) {
    fun findBottom(point: Pair<Int, Int>): Pair<Int, Int> {
        val blocks = rock + sand
        var lastPoint = point
        var nextPoint = Pair(point.first, point.second + 1)

        while (!blocks.contains(nextPoint) && !outBottom(nextPoint)) {
            lastPoint = nextPoint
            nextPoint = Pair(lastPoint.first, lastPoint.second + 1)
        }

        return lastPoint
    }

    fun findAction(point: Pair<Int, Int>): Pair<Action, Pair<Int, Int>> {
        val blocks = rock + sand
        var left = Pair(point.first - 1, point.second)
        var right = Pair(point.first + 1, point.second)
        var downleft = Pair(point.first - 1, point.second + 1)
        var downright = Pair(point.first + 1, point.second + 1)
        var bottom = Pair(point.first, point.second + 1)
        return if (!blocks.contains(bottom)) {
            Pair(Action.DOWN, bottom)
        } else if (!blocks.contains(downleft)) {
            Pair(Action.LEFT, downleft)
        } else if (!blocks.contains(downright)) {
            Pair(Action.RIGHT, downright)
        } else {
            Pair(Action.STOP, point)
        }
    }

    var currentBottom = findBottom(start)
    var currentAction = findAction(currentBottom)

    while (currentAction.first != Action.STOP && !outBottom(currentAction.second)) {
        if (currentAction.first == Action.DOWN) {
            currentBottom = findBottom(currentBottom)
            currentAction = findAction(currentBottom)
        } else {
            currentBottom = currentAction.second
            currentAction = findAction(currentBottom)
        }
    }

    if (!outBottom(currentAction.second)) {
        sand += currentAction.second
    } else {
        done = true
    }

}

draw()
println("Round ${sand.size}")
