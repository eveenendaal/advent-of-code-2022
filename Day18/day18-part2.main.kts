import java.io.File
import kotlin.math.absoluteValue

data class Point(val x: Int, val y: Int, val z: Int)

fun Point.distance(point: Point): Int {
    return (this.x - point.x).absoluteValue +
            (this.y - point.y).absoluteValue +
            (this.z - point.z).absoluteValue
}

fun Point.exposedSides(): Int {
    val blockedSizes = points.count { it.distance(this) == 1 }
    return 6 - blockedSizes
}

fun Point.neighbors(): Set<Point> {
    return points.filter { it.distance(this) == 1 }.toSet()
}

fun Point.touchingPoints(): Set<Point> {
    return setOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
        copy(z = z - 1),
        copy(z = z + 1)
    )
}

fun Set<Point>.rangeOf(function: (Point) -> Int): IntRange {
    return this.minOf(function) - 1..this.maxOf(function) + 1
}

fun readInput(): Set<Point> {
    return File("day18-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .map { it.split(",") }
        .map { Point(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
        .toSet()
}

val points = readInput()

val xRange = points.rangeOf { it.x }
val yRange = points.rangeOf { it.y }
val zRange = points.rangeOf { it.z }

val queue = ArrayDeque<Point>().apply { add(Point(xRange.first, yRange.first, zRange.first)) }
val seen = mutableSetOf<Point>()
var sidesFound = 0
queue.forEach { lookNext ->
    if (lookNext !in seen) {
        lookNext.touchingPoints()
            .filter { it.x in xRange && it.y in yRange && it.z in zRange }
            .forEach { neighbor ->
                seen += lookNext
                if (neighbor in points) {
                    sidesFound++
                } else {
                    queue.add(neighbor)
                }
            }
    }
}


println(sidesFound)
