import java.io.File

data class Point(var x: Int, var y: Int, var height: Int, var visited: Boolean = false)

class Map(var grid: List<List<Point>>) {
    fun get(x: Int, y: Int): Point? {
        return grid.flatten().firstOrNull { it.x == x && it.y == y }
    }

    fun maxHeight(): Int {
        return map.grid.flatten().map { it.height }.max()
    }
}

fun parseInput(): List<List<Point>> {
    val letters = "SabcdefghijklmnopqrstuvwxyzE"
    var y = 0
    return File("day12-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }
        .map { line ->
            var x = 0
            val row: List<Point> = line.toCharArray().asList().map {
                val next = Point(x, y, letters.indexOf(it))
                x += 1
                next
            }
            y += 1
            row
        }
}

val map = Map(parseInput())

val allPoints = map.grid.flatten()
val start = allPoints.minBy { it.height }
var end = allPoints.maxBy { it.height }

val lookupMap = mutableMapOf<Point, List<Point>>()
allPoints.map { nextPoint ->
    val points = listOfNotNull(
        map.get(nextPoint.x - 1, nextPoint.y),
        map.get(nextPoint.x + 1, nextPoint.y),
        map.get(nextPoint.x, nextPoint.y - 1),
        map.get(nextPoint.x, nextPoint.y + 1)
    )
        .filter { allPoints.contains(it) }
        .filter { it.height <= nextPoint.height + 1 }
    lookupMap.put(nextPoint, points)
}

fun findShortestPath(): Int? {
    val toVisit: MutableList<Point> = listOf(start).toMutableList()

    var moves = 0
    while (toVisit.isNotEmpty()) {
        moves += 1
        val nextToVisit = mutableSetOf<Point>()
        toVisit.forEach { next ->
            if (!next.visited) {
                nextToVisit.addAll(lookupMap[next]!!)
                next.visited = true
            }
        }

        println(moves)

        if (nextToVisit.any { it.height == end.height }) {
            return moves
        } else {
            toVisit.clear()
            toVisit.addAll(nextToVisit)
        }
    }

    return Int.MAX_VALUE
}

val result = findShortestPath()
println(result)
