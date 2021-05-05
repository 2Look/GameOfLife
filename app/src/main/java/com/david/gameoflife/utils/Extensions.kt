package com.david.gameoflife.utils

import com.david.gameoflife.persistance.Construct
import com.david.gameoflife.screens.ConstructData
import com.david.gameoflife.utils.Serialization.parseCoordinates

fun Set<Pair<Int, Int>>.potentialCandidates() = this.flatMap {
    GameUtils.neighboursFor(it.first, it.second)
}

fun CellSet.normalize(): CellSet {
    val minX = this.map { it.first }.minOrNull()
    val minY = this.map { it.second }.minOrNull()
    return this.map { it.first - (minX ?: 0) to it.second - (minY ?: 0) }.toSet()
}

fun List<Construct>.mapToConstructData() = map {
    ConstructData(
        it.uid,
        it.name,
        it.coordinates.parseCoordinates()
    )
}

val <T> T.exhaustive get() = this

