import java.io.File

fun readInput() {
    val inputRanges = File("dayN-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray()
        .filter { it.isNotEmpty() }

}
