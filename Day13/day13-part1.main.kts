@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

fun parseInput(): List<Pair<Any, Any>> {
    val packets = emptyList<Any>().toMutableList()
    val pairs = emptyList<Pair<Any, Any>>().toMutableList()

    File("day13-input.txt").inputStream()
        .bufferedReader().use { it.readText() }
        .split("\\R".toRegex()).toTypedArray().asList()
        .forEach { line ->
            if (line.isNotEmpty()) {
                packets += flatten(Json.parseToJsonElement(line))
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
    } else {
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

        return if (leftList.size < rightList.size) {
            true
        } else if (leftList.size > rightList.size) {
            false
        } else {
            null
        }
    }
}

fun flatten(node: Any): Any {
    return when (node) {
        is JsonArray -> node.toList().map {
            flatten(it)
        }

        is JsonPrimitive -> node.content.toInt()

        else -> throw RuntimeException("Unknown Element")
    }
}

var total = 0
var count = 1
pairs
    .map { Pair(it.first, it.second) }
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
