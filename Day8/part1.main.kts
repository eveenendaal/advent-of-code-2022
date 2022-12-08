import java.io.File

data class Tree(val height: Int, val row: Int, val column: Int)
data class Row(var trees: List<Tree> = emptyList())
data class Column(var trees: List<Tree> = emptyList())

var rows = emptyList<Row>()
var columns = emptyList<Column>()
var trees = emptyList<Tree>()

var row = 0
val inputRanges = File("input.txt").inputStream()
    .bufferedReader().use { it.readText() }
    .split("\\R".toRegex()).toTypedArray()
    .filter { it.isNotEmpty() }
    .forEach {
        var column = 0
        var trees = it.toCharArray().toList().map {
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

fun isVisible(tree: Tree, column: Column, row: Row): Boolean {
    var left = row.trees.subList(0, tree.column)
    var right = row.trees.subList(tree.column + 1, row.trees.size)
    var top = column.trees.subList(0, tree.row)
    var bottom = column.trees.subList(tree.row + 1, column.trees.size)

    var leftVisible = left.none { it.height >= tree.height }
    var rightVisible = right.none { it.height >= tree.height }
    var topVisible = top.none { it.height >= tree.height }
    var bottomVisible = bottom.none { it.height >= tree.height }

    println("$tree (left $leftVisible, right $rightVisible, top $topVisible, bottom $bottomVisible,)")

    return leftVisible || rightVisible || topVisible || bottomVisible
}

val count = trees.count { tree ->
    isVisible(
        tree,
        columns.first { it.trees.contains(tree) },
        rows.first { it.trees.contains(tree) }
    )
}

println(count)
