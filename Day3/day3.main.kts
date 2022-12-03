import java.io.File

// Read Input
val inputStrings = File("test.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()

val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
var total = 0
var sacks = emptyArray<Sack>()

data class Sack(var letter: Char, var value: Int)

inputStrings
    .filter { it.isNotEmpty() }
    .forEach { line ->
        val halfWay = line.length / 2

        val compartment1 = line.substring(0, halfWay)
        val compartment2 = line.substring(halfWay)

        var shared = emptyArray<Char>()
        compartment1.toCharArray().forEach {
            if (compartment2.toCharArray().contains(it) && !shared.contains(it)) {
                shared += it
            }
        }

        if (shared.size != 1) {
            throw RuntimeException("Too few/many charaters ${shared.size}")
        }

        val sharedCharacter = shared.first()
        var valueCounter = 0
        var finalValue = 0
        letters.toCharArray().forEach {
            valueCounter += 1
            if (it == sharedCharacter) {
                finalValue = valueCounter
            }
        }
        if (finalValue == 0) {
            throw RuntimeException("Not value found for $sharedCharacter")
        }

        sacks += Sack(sharedCharacter, finalValue)
        println("$compartment1 (${compartment1.length}) + $compartment2 (${compartment2.length}) = $line ($sharedCharacter = $finalValue)")
    }


println(sacks.sumOf { it.value })
sacks.sortBy { it.letter }
println(sacks)


