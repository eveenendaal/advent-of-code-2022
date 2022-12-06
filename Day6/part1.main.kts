import java.io.File

File("test.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach {
        findFirstMarker(it.toCharArray().toTypedArray().asList())
    }

fun findFirstMarker(input: List<Char>): Int {
    val characters: MutableList<Char> = mutableListOf()
    var letterCount = 0
    var uniqueCount = 0
    input.forEach {
        letterCount += 1
        if (characters.contains(it)) {
            uniqueCount = 0
        } else {
            uniqueCount += 1
            characters += it
        }

        if (uniqueCount == 4) {
            println("Marker at '${letterCount - 3}' ($characters)")
            return letterCount - 3
        }
    }
    return -1
}

