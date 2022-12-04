import java.io.File

// Read Input
val inputStrings = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { it.toCharArray() }

val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
var total = 0
var groups = emptyArray<Group>()

data class Group(var letter: Char, var value: Int)

val listIterator = inputStrings.listIterator()

fun calcValue(char: Char): Int {
    var valueCounter = 0
    letters.toCharArray().forEach {
        valueCounter += 1
        if (it == char) {
            return valueCounter
        }
    }
    throw RuntimeException("Not value found for $char")
}

fun findShared(part1: CharArray, part2: CharArray): CharArray {
    var shared = "".toCharArray()
    part1.forEach {
        if (part2.contains(it) && !shared.contains(it)) {
            shared += it
        }
    }
    return shared
}

while (listIterator.hasNext()) {
    val elf1 = listIterator.next()
    val elf2 = listIterator.next()
    val elf3 = listIterator.next()
    val common = findShared(findShared(elf1, elf2), elf3)
    if (common.size != 1) {
        throw RuntimeException("Too few/many charaters ${common.size}")
    }
    groups += Group(common.first(), calcValue(common.first()))
}

groups.sortBy { it.letter }

groups.forEach { println(it) }

println(groups.sumOf { it.value })
