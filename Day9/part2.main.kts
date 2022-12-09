import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max

enum class Direction(val value: String) {
    Left("L"),
    Right("R"),
    Down("D"),
    Up("U")

}

data class Position(var x: Int = 0, var y: Int = 0)

data class Entry(val direction: Direction, val count: Int)

val inputs = File("test.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { next ->
        val parts = next.split(" ")
        Entry(Direction.values().first { it.value == parts[0] }, parts[1].toInt())
    }

val head = Position()

fun calculateDistance(p1: Position, p2: Position): Int {
    return max((p1.x - p2.x).absoluteValue, (p1.y - p2.y).absoluteValue)
}

fun adjustPosition(p1: Position, p2: Position, direction: Direction) {
    var distance = calculateDistance(p1, p2)
    if (distance > 1) {

        when (direction) {
            Direction.Left -> {
                p2.x -= 1
                p2.y = p1.y
            }

            Direction.Right -> {
                p2.x += 1
                p2.y = p1.y
            }

            Direction.Down -> {
                p2.y -= 1
                p2.x = p1.x
            }

            Direction.Up -> {
                p2.y += 1
                p2.x = p1.x
            }
        }

        distance = calculateDistance(p1, p2)
        if (distance > 1) {
            throw RuntimeException("Distance Not Fixed (Head: $p1 Tail: $p2 Direction: $direction)")
        }

    }
}

fun printGrid(head: Position, points: List<Position>) {
    val allPoints = listOf(head) + points
    val maxX = allPoints.maxOf { it.x }
    val maxY = allPoints.maxOf { it.y }

    (0..maxY).forEach { y ->
        val line = (0..maxX).map { x ->
            var result = "."
            val position = allPoints.firstOrNull { it.x == x && it.y == y }
            if (position != null) {
                val index = allPoints.indexOf(position)
                result = if (index == 0) {
                    "H"
                } else {
                    "$index"
                }
            }
            result
        }
        println(line)
    }
}

val points = (1..9).map { Position() }
var tailHistory = setOf(points.last().copy())

inputs.forEach { input ->
    (1..input.count).forEach {
        // Move Head
        when (input.direction) {
            Direction.Left -> head.x -= 1
            Direction.Right -> head.x += 1
            Direction.Down -> head.y -= 1
            Direction.Up -> head.y += 1
        }

        val pointIterator = points.iterator()

        var lastPoint = pointIterator.next()
        adjustPosition(head, lastPoint, input.direction)
        while (pointIterator.hasNext()) {
            var nextPoint = pointIterator.next()
            adjustPosition(lastPoint, nextPoint, input.direction)
            lastPoint = nextPoint
        }

        // Result
        tailHistory += points.last().copy()

        printGrid(head, points)
    }
}

println(tailHistory.size)
