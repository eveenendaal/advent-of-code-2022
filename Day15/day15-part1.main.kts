import java.io.File
import kotlin.math.absoluteValue

data class Point(val x: Long, val y: Long)

var sensors = setOf<Point>()
var beacons = setOf<Point>()
var empty = setOf<Point>()

var ranges = setOf<LongRange>().toMutableSet()

val target = 2000000L

fun loadInput() {
    val coors = "x=([\\-0-9]+), y=([\\-0-9]+)".toRegex()

    fun calcDistance(p1: Point, p2: Point): Long {
        return (p1.x - p2.x).absoluteValue + (p1.y - p2.y).absoluteValue
    }

    File("day15-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .forEach { line ->
            val points = line.split(":")
                .map { coors.find(it) }
                .map { it!!.value }
                .map { "[\\-0-9]+".toRegex().findAll(it) }
                .map { it.toList() }
                .map { Point(it[0].value.toLong(), it[1].value.toLong()) }

            val sensor = points[0]
            val beacon = points[1]

            val distance = calcDistance(sensor, beacon)

            val minX = sensor.x - distance
            val maxX = sensor.x + distance
            val minY = sensor.y - distance
            val maxY = sensor.y + distance

            val diff = distance - (target - sensor.y).absoluteValue
            val startX = sensor.x - diff
            val endX = sensor.x + diff

            if (target in minY..maxY) {
                ranges += LongRange(startX, endX)
            }

            sensors += sensor
            beacons += beacon
        }
}

fun printGrid() {
    val values = sensors + beacons + empty
    val minX = values.map { it.x }.min()
    val maxX = values.map { it.x }.max()
    val minY = values.map { it.y }.min()
    val maxY = values.map { it.y }.max()

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->

            val point = Point(x, y)
            if (sensors.contains(point)) {
                print("S")
            } else if (beacons.contains(point)) {
                print("B")
            } else if (empty.contains(point)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }

}

loadInput()
//printGrid()
println(ranges.toList().reduce())

fun List<LongRange>.reduce(): List<LongRange> =
    if (this.size <= 1) {
        this
    } else {
        val sorted = this.sortedBy { it.first }
        sorted.drop(1).fold(mutableListOf(sorted.first())) { reduced, range ->
            val lastRange = reduced.last()
            if (range.first <= lastRange.last)
                reduced[reduced.lastIndex] = (lastRange.first..maxOf(lastRange.last, range.last))
            else
                reduced.add(range)
            reduced
        }
    }

val final = ranges.toList().reduce().sumOf { it.last - it.first }
println(final)
