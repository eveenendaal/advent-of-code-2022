import java.io.File

enum class Direction(val value: String) {
    Left("L"),
    Right("R"),
    Down("D"),
    Up("U"),
    UpLeft("UL"),
    UpRight("UR"),
    DownLeft("DL"),
    DownRight("DR")
}

data class Position(var number: Int, var x: Int = 0, var y: Int = 0)

data class Entry(val direction: Direction, val count: Int)

val inputs = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { next ->
        val parts = next.split(" ")
        Entry(Direction.values().first { it.value == parts[0] }, parts[1].toInt())
    }

val head = Position(0)

fun movePoint(point: Position, direction: Direction) {
    when (direction) {
        Direction.Left -> point.x -= 1
        Direction.Right -> point.x += 1
        Direction.Down -> point.y -= 1
        Direction.Up -> point.y += 1
        Direction.UpLeft -> {
            point.x -= 1
            point.y += 1
        }

        Direction.UpRight -> {
            point.x += 1
            point.y += 1
        }

        Direction.DownLeft -> {
            point.x -= 1
            point.y -= 1
        }

        Direction.DownRight -> {
            point.x += 1
            point.y -= 1
        }
    }
}

fun adjustPosition(p1: Position, p2: Position) {
    val xdiff = p1.x - p2.x
    val ydiff = p1.y - p2.y

    val action: Direction? = when (xdiff) {
        -2 -> {
            when (ydiff) {
                -2, -1 -> Direction.DownLeft
                0 -> Direction.Left
                1, 2 -> Direction.UpLeft
                else -> null
            }
        }

        -1 -> {
            when (ydiff) {
                -2 -> Direction.DownLeft
                2 -> Direction.UpLeft
                else -> null
            }
        }

        0 -> {
            when (ydiff) {
                -2 -> Direction.Down
                2 -> Direction.Up
                else -> null
            }
        }

        1 -> {
            when (ydiff) {
                -2 -> Direction.DownRight
                2 -> Direction.UpRight
                else -> null
            }
        }

        2 -> {
            when (ydiff) {
                -2, -1 -> Direction.DownRight
                0 -> Direction.Right
                1, 2 -> Direction.UpRight
                else -> null
            }
        }

        else -> null
    }

    if (action != null) {
        movePoint(p2, action)
    }
}

fun printGrid(allPoints: List<Position>, tail: List<Position>) {
    val allValues = allPoints.map { it.x } + allPoints.map { it.y }
    val min = allValues.min()
    val max = allValues.max()

    println(allPoints)

    (min..max).reversed().forEach { y ->
        val line = (min..max).map { x ->
            var result: String? = null
            val position = allPoints.firstOrNull { it.x == x && it.y == y }
            if (position != null) {
                val index = allPoints.indexOf(position)
                result = if (index == 0) {
                    "H"
                } else {
                    "$index"
                }
            }

            if (result == null) {
                if (x == 0 && y == 0) {
                    result = "s"
                } else if (tail.any { it.x == x && it.y == y }) {
                    result = "#"
                }
            }

            result ?: "."
        }
        println(line)
    }
    println()
}

val points = (1..9).map { Position(it) }
var tailHistory = setOf(points.last().copy())

inputs.forEach { input ->
    (1..input.count).forEach {
        // Move Head
        movePoint(head, input.direction)
        val pointIterator = points.iterator()

        var lastPoint = head
        var nextPoint: Position?
        while (pointIterator.hasNext()) {
            nextPoint = pointIterator.next()
            adjustPosition(lastPoint, nextPoint)
            lastPoint = nextPoint
        }
        println()

        // Result
        tailHistory += points.last().copy()
    }
}
printGrid(listOf(head) + points, tailHistory.toList())

println(tailHistory.size)
