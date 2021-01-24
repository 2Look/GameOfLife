package com.david.gameoflife

import androidx.compose.ui.res.booleanResource
import org.junit.Test

import org.junit.Assert.*

class BoardTest {

    @Test
    fun `one cell dies`() {
        val board = Board()
        board.toggleCell(0,0)
        board.nextGeneration()
        assert(board.matrix[0][0] == Cell.Dead)
    }

    @Test
    fun `four cells survive`(){
        val board = Board()
        board.toggleCell(0,0)
        board.toggleCell(1,0)
        board.toggleCell(0,1)
        board.toggleCell(1,1)
        board.nextGeneration()
        assert(board.matrix[0][0] == Cell.Alive)
        assert(board.matrix[1][0] == Cell.Alive)
        assert(board.matrix[0][1] == Cell.Alive)
        assert(board.matrix[1][1] == Cell.Alive)
    }

    @Test
    fun `three cells multiply`() {
        val board = Board()
        board.toggleCell(0,0)
        board.toggleCell(0,1)
        board.toggleCell(1,0)
        board.nextGeneration()
        assert(board.matrix[1][1] == Cell.Alive)
    }
}