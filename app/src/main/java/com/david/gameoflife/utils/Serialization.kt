package com.david.gameoflife.utils

object Serialization {
    fun String.parseCoordinates() =
        lines()
            .filter { it.isNotEmpty() }
            .map { line ->
                val tokens = line.split(' ')
                Pair(tokens[0].toInt(), tokens[1].toInt())
            }
            .toSet()

    fun CellSet.serialize(): String {
        val cellSet = this
        return buildString {
            cellSet.map { pair ->
                "${pair.first} ${pair.second}"
            }.forEach {
                append(it)
                append('\n')
            }
        }
    }
}