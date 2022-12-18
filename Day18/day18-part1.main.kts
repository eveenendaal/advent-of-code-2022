import java.io.File
import kotlin.math.absoluteValue

data class Point(val x: Int, val y: Int, val z: Int)

fun Point.distance(point: Point): Int {
    return (this.x - point.x).absoluteValue +
            (this.y - point.y).absoluteValue +
            (this.z - point.z).absoluteValue
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

val sizes = points.sumOf { point ->
    val blockedSizes = this.points.count { it.distance(point) == 1 }
    6 - blockedSizes
}

println(sizes)
