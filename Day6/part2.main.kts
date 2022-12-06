import java.io.File

File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach {
        findFirstMarker(it.toCharArray().toTypedArray().asList())
    }

fun findFirstMarker(input: List<Char>): Int {
    var messageBuffer: MutableList<Char> = mutableListOf()

    var letterCount = 0
    input.forEach {
        letterCount += 1

        messageBuffer += it
        if (messageBuffer.size == 15) {
            messageBuffer = messageBuffer.subList(1, 15)
        }

        if (messageBuffer.size == 14) {
            if (messageBuffer.toSet().size == messageBuffer.size) {
                println("Message Start $messageBuffer at $letterCount")
                return letterCount
            }
        }

    }

    println("Not Message Start Found")
    return -1
}

