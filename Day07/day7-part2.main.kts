import java.io.File

val inputRanges = File("day7-input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }

data class MyFile(val name: String, val size: Int)
data class MyFolder(
    val name: String,
    val parent: MyFolder? = null,
    var files: List<MyFile> = emptyList(),
    var folders: List<MyFolder> = emptyList(),
)

val rootFolder = MyFolder("root")
var currentFolder = rootFolder

inputRanges.forEach { nextLine ->
    if (nextLine.startsWith("$")) { // Command
        val input = nextLine.substring(2)
        val inputParts = input.split(" ")
        when (inputParts[0]) {
            "cd" -> {
                val folder = inputParts[1]
                currentFolder = when (folder) {
                    "/" -> rootFolder
                    ".." -> currentFolder.parent!!
                    else -> currentFolder.folders.first { it.name == folder }
                }
            }

            "ls" -> {

            }
        }
    } else {
        val inputParts = nextLine.split(" ")
        if (inputParts[0] == "dir") {
            currentFolder.folders += MyFolder(inputParts[1], parent = currentFolder)
        } else {
            currentFolder.files += MyFile(inputParts[1], inputParts[0].toInt())
        }
    }
}

data class FolderSummary(val name: String, val size: Int)

var folders = emptyList<FolderSummary>()

fun printfolder(folder: MyFolder, level: Int): Int {
    val space = " ".repeat(level)
    val size = folder.files.sumOf { it.size }
    var total = size


    folder.files.forEach {
        // println("$space- ${it.name} (${it.size})")
    }

    folder.folders.forEach {
        total += printfolder(it, level + 2)
    }
    folders += FolderSummary(folder.name, total)
    println("$space${folder.name} ($total)")

    return total
}

val total = printfolder(rootFolder, 0)
println("Grant Total: $total")

val diskTotal = 70000000
val spaceNeeded = 30000000
val spaceAvailable = 70000000 - total
val toDelete = spaceNeeded - spaceAvailable

val answer = folders
    .sortedBy { it.size }
    .filter { it.size > toDelete }

println(answer)
println(answer.first())
