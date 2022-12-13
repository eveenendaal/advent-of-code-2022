import java.io.File

data class Node(var parent: Node?, var children: List<Any> = emptyList())

fun parseInput(): List<Pair<Node, Node>> {
    val packets = emptyList<Node>().toMutableList()
    val pairs = emptyList<Pair<Node, Node>>().toMutableList()

    File("input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray().asList()
        .forEach { line ->
            var current: Node? = null
            var buffer = ""
            if (line.isNotEmpty()) {
                line.toCharArray().asList()
                    .map { it.toString() }
                    .forEach {
                        if (it == "[") {
                            current = Node(current)
                            if (current!!.parent != null) {
                                current!!.parent!!.children += current!!
                            }
                        } else if (it == "]") {
                            if (buffer.isNotEmpty()) {
                                current!!.children += buffer.toInt()
                                buffer = ""
                            }

                            if (current!!.parent == null) {
                                packets += current!!
                                current = null
                            } else {
                                var parent = current!!.parent!!
                                current!!.parent = null
                                current = parent
                            }
                        } else if (it == ",") {
                            if (buffer.isNotEmpty()) {
                                current!!.children += buffer.toInt()
                                buffer = ""
                            }
                        } else {
                            buffer += it
                        }

                    }
            } else {
                if (packets.size != 2) {
                    throw RuntimeException("Incorrect packet count ${packets.size}")
                }
                pairs.add(Pair(packets[0], packets[1]))
                packets.clear()
            }
        }

    return pairs
}

val pairs = parseInput()

fun compare(left: Any, right: Any): Boolean? {
    println("Compare $left vs $right")

    if (left is Int && right is Int) {
        println("Compare $left vs $right")
        if (left < right) {
            return true
        } else if (left > right) {
            return false
        }
        return null
    } else  {
        val leftList = if (left is Int) listOf(left) else left as List<Any>
        val rightList = if (right is Int) listOf(right) else right as List<Any>

        val leftIter = leftList.iterator()
        val rightIter = rightList.iterator()

        while (leftIter.hasNext() && rightIter.hasNext()) {
            val result = compare(leftIter.next(), rightIter.next())
            if (result != null) {
                return result
            }
        }

        if (leftList.size < rightList.size) {
            return true
        } else if (leftList.size > rightList.size) {
            return false
        } else {
            return null
        }
    }
}

fun flatten(list: List<Any>): List<Any> {
    return list.map {
        return@map if (it is Node) {
            flatten(it.children)
        } else {
            it
        }
    }
}

var total = 0
var count = 1
pairs
    .map { Pair(flatten(it.first.children), flatten(it.second.children)) }
    .forEach { pair ->
        println("== Pair $count ==")
        val result = compare(pair.first, pair.second)
        if (result != false) {
            println("Pair $count) Correct Order")
            total += count
        } else {
            println("Pair $count) Wrong Order")
        }
        count += 1
        println()
    }

println(total)


