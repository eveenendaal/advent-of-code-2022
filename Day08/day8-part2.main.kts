import java.io.File

data class Tree(val height: Int, val row: Int, val column: Int)
data class Row(var trees: List<Tree> = emptyList())
data class Column(var trees: List<Tree> = emptyList())

var rows = emptyList<Row>()
var columns = emptyList<Column>()
var trees = emptyList<Tree>()

var row = 0
val inputRanges = File("day8-input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach {
        var column = 0
        val trees = it.toCharArray().toList().map {
            val tree = Tree(it.toString().toInt(), row, column)
            if (columns.getOrNull(column) == null) {
                columns += Column()
            }
            columns.get(column).trees += tree
            trees += tree
            column += 1
            tree
        }
        row += 1
        rows += Row(trees)
    }

data class TreeSummary(val tree: Tree, val score: Int, val values: List<Int>)

fun isVisible(tree: Tree, column: Column, row: Row): TreeSummary {
    val left = row.trees.subList(0, tree.column)
    val right = row.trees.subList(tree.column + 1, row.trees.size)
    val top = column.trees.subList(0, tree.row)
    val bottom = column.trees.subList(tree.row + 1, column.trees.size)

    var leftVisibility = 0
    var rightVisibility = 0
    var topVisibility = 0
    var bottomVisibility = 0

    var maxFound = false
    left.reversed().forEach {
        if (!maxFound) leftVisibility += 1
        if (it.height >= tree.height) maxFound = true
    }

    maxFound = false
    top.reversed().forEach {
        if (!maxFound) topVisibility += 1
        if (it.height >= tree.height) maxFound = true
    }

    maxFound = false
    right.forEach {
        if (!maxFound) rightVisibility += 1
        if (it.height >= tree.height) maxFound = true
    }

    maxFound = false
    bottom.forEach {
        if (!maxFound) bottomVisibility += 1
        if (it.height >= tree.height) maxFound = true
    }

    println("$tree (left $leftVisibility, right $rightVisibility, top $topVisibility, bottom $bottomVisibility)")

    return TreeSummary(
        tree,
        leftVisibility * rightVisibility * topVisibility * bottomVisibility,
        listOf(leftVisibility, rightVisibility, topVisibility, bottomVisibility)
    )
}

val count = trees.map { tree ->
    isVisible(
        tree,
        columns.first { it.trees.contains(tree) },
        rows.first { it.trees.contains(tree) }
    )
}.maxBy { it.score }

println(count)
