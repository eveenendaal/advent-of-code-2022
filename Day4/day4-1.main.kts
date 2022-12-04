import java.io.File

val inputRanges = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .map { it.split(",") }
    .map { Pair(it[0], it[1]) }
    .map {
        val firstEnds = it.first.split("-")
        val secondEnds = it.second.split("-")
        val firstRange = (firstEnds[0].toInt()..firstEnds[1].toInt()).toList()
        val secondRange = (secondEnds[0].toInt()..secondEnds[1].toInt()).toList()
        Pair(firstRange, secondRange)
    }

val contained = inputRanges
    .map {
        it.first.containsAll(it.second) || it.second.containsAll(it.first)
    }

println("${contained.count { it }} contained of ${contained.size}")


