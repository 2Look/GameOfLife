package com.david.gameoflife.utils

fun Set<Pair<Int, Int>>.potentialCandidates() = this.flatMap {
    GameUtils.neighboursFor(it.first, it.second)
}

val <T> T.exhaustive get() = this

