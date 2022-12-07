import java.io.File

val inputRanges = File("test.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }


inputRanges.forEach {
    println(it)
}

