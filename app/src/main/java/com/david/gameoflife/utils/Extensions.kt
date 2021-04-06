package com.david.gameoflife.utils

fun Set<Pair<Int, Int>>.potentialCandidates() = this.flatMap {
    GameUtils.neighboursFor(it.first, it.second)
}

fun CellSet.normalize(): CellSet {
    val minX = this.map { it.first }.minOrNull()
    val minY = this.map { it.second }.minOrNull()
    return this.map { it.first - (minX ?: 0) to it.second - (minY ?: 0) }.toSet()
}

val <T> T.exhaustive get() = this

