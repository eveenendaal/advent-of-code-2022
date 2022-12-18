import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Point(val x: Long, val y: Long, var distance: Long? = null)

fun Point.distanceTo(point: Point): Long {
    return (this.x - point.x).absoluteValue + (this.y - point.y).absoluteValue
}

fun Point.same(point: Point): Boolean {
    return this.x == point.x && this.y == point.y
}

fun Point.lineTo(other: Point): List<Point> {
    val xDelta = (other.x - x).sign
    val yDelta = (other.y - y).sign
    val steps = maxOf((x - other.x).absoluteValue, (y - other.y).absoluteValue)
    return (1..steps).scan(this) { last, _ -> Point(last.x + xDelta, last.y + yDelta) }
}

var sensors = setOf<Point>()
var beacons = setOf<Point>()

var ranges = setOf<LongRange>().toMutableSet()

val caveSize = 4000000.toLong()

fun loadInput() {
    val coors = "x=([\\-0-9]+), y=([\\-0-9]+)".toRegex()

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
            sensor.distance = sensor.distanceTo(beacon)

            val distance = sensor.distanceTo(beacon)

            val minX = sensor.x - distance
            val maxX = sensor.x + distance
            val minY = sensor.y - distance
            val maxY = sensor.y + distance

            val diff = distance - (caveSize - sensor.y).absoluteValue
            val startX = sensor.x - diff
            val endX = sensor.x + diff

            if (caveSize in minY..maxY) {
                ranges += LongRange(startX, endX)
            }

            sensors += sensor
            beacons += beacon
        }
}

fun printGrid() {
    val minX = sensors.map { it.x }.min() - caveSize
    val maxX = sensors.map { it.x }.max() + caveSize
    val minY = sensors.map { it.y }.min() - caveSize
    val maxY = sensors.map { it.y }.max() + caveSize

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->

            val point = Point(x, y)
            var isRangeOfSensor = sensors.any {
                it.distanceTo(point) <= caveSize
            }

            var isEmpty = sensors
                .filter { it.distanceTo(point) <= caveSize }
                .any { it.distanceTo(point) <= it.distance!! }

            if (sensors.any { it.same(point) }) {
                print("S")
            } else if (beacons.any { it.same(point) }) {
                print("B")
            } else if (isRangeOfSensor) {
                if (isEmpty) {
                    print("#")
                } else if (point.x >= 0 && point.y >= 0 && point.x <= caveSize && point.y <= caveSize) {
                    print(".")
                } else {
                    print(" ")
                }
            } else {
                print(" ")
            }
        }
        println()
    }

}

loadInput()
// printGrid()

val caveRange = (0..caveSize)
val answer = this.sensors
    .firstNotNullOf { sensor ->
        val up = Point(sensor.x, sensor.y - sensor.distance!! - 1)
        val down = Point(sensor.x, sensor.y + sensor.distance!! + 1)
        val left = Point(sensor.x - sensor.distance!! - 1, sensor.y)
        val right = Point(sensor.x + sensor.distance!! + 1, sensor.y)

        val searchPoints = (up.lineTo(right) + right.lineTo(down) + down.lineTo(left) + left.lineTo(up))
            .filter { it.x in caveRange && it.y in caveRange }
            .toSet()

        var solutions = searchPoints
            .firstOrNull { candidate -> sensors.none { sensor -> sensor.distanceTo(candidate) <= sensor.distance!! } }

        return@firstNotNullOf solutions
    }

println(answer)
println(4000000L * answer.x + answer.y)
