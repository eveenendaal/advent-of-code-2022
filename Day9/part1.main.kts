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

val inputs = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { next ->
        val parts = next.split(" ")
        Entry(Direction.values().first { it.value == parts[0] }, parts[1].toInt())
    }

val head = Position()
val tail = Position()

var tailHistory = setOf(tail.copy())

fun calculateDistance(p1: Position, p2: Position): Int {
    return max((p1.x - p2.x).absoluteValue, (p1.y - p2.y).absoluteValue)
}

inputs.forEach { input ->
    (1..input.count).forEach {
        // Move Head
        when (input.direction) {
            Direction.Left -> head.x -= 1
            Direction.Right -> head.x += 1
            Direction.Down -> head.y -= 1
            Direction.Up -> head.y += 1
        }

        // Update Tail
        var distance = calculateDistance(head, tail)
        if (distance > 1) {

            when (input.direction) {
                Direction.Left -> {
                    tail.x -= 1
                    tail.y = head.y
                }

                Direction.Right -> {
                    tail.x += 1
                    tail.y = head.y
                }

                Direction.Down -> {
                    tail.y -= 1
                    tail.x = head.x
                }

                Direction.Up -> {
                    tail.y += 1
                    tail.x = head.x
                }
            }

            distance = calculateDistance(head, tail)
            if (distance > 1) {
                throw RuntimeException("Distance Not Fixed (Head: $head Tail: $tail Direction: ${input.direction})")
            }

        }

        // Result
        distance = calculateDistance(head, tail)
        tailHistory += tail.copy()
        println("Head: $head Tail: $tail Direction: ${input.direction} Distance: $distance")
    }
}

println(tailHistory.size)
