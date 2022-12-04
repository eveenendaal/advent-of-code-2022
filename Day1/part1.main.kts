import java.io.File

val input = File("input.txt").inputStream().bufferedReader()
    .use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()

data class Elf(val number: Int, val total: Int)

var elfNumber = 1
var total = 0
var results = listOf<Elf>()
input.forEach { line ->
    if (line.isEmpty()) {
        results += listOf(Elf(elfNumber, total))
        elfNumber += 1
        total = 0
    } else {
        total += line.toInt()
    }
}

val sortedResults = results.sortedBy { it.total }.reversed()
var grandTotal = 0
sortedResults.subList(0, 3).forEach {
    grandTotal += it.total
}

println(sortedResults.subList(0, 3))
println(grandTotal)
