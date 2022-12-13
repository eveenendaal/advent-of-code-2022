import java.io.File

File("day6-input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach {
        findFirstMarker(it.toCharArray().toTypedArray().asList())
    }

fun findFirstMarker(input: List<Char>): Int {
    var characters: MutableList<Char> = mutableListOf()
    var letterCount = 0
    input.forEach {
        letterCount += 1
        characters += it
        if (characters.size == 5) {
            characters = characters.subList(1, 5)
        }

        if (characters.size == 4) {
            if (characters.toSet().size == characters.size) {
                println("Unique Set $characters at $letterCount")
                return letterCount - 3
            }
        }
    }
    return -1
}

