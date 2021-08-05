package com.david.gameoflife.utils

import androidx.compose.ui.geometry.Offset
import com.david.gameoflife.persistance.Construct
import com.david.gameoflife.screens.ConstructData
import com.david.gameoflife.utils.Serialization.parseCoordinates
import kotlin.math.max

fun Set<Pair<Int, Int>>.potentialCandidates() = this.flatMap {
    GameUtils.neighboursFor(it.first, it.second)
}

fun CellSet.offset(offset: Offset) =
    map { it.first + offset.x.toInt() to it.second + offset.y.toInt() }.toSet()

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

fun ConstructData.dimensions(): Pair<Int, Int> {
    val width = cells.map { it.second }.maxOrNull() ?: 0
    val height = cells.map { it.first }.maxOrNull() ?: 0
    return width to height
}

fun Int.notZero() = if (this == 0) this + 1 else this

val <T> T.exhaustive get() = this

