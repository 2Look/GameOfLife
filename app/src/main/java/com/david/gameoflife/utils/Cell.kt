package com.david.gameoflife.utils

sealed class Cell {
    object Alive : Cell()
    object Dead : Cell()

    fun isAlive() = this == Alive
    fun isDead() = !isAlive()
}