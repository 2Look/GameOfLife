package com.david.gameoflife

import com.david.gameoflife.utils.GameUtils
import com.david.gameoflife.utils.GameUtils.nextGeneration
import org.junit.Test

class BoardTest {

    @Test
    fun `one cell dies`() {
        val viewModel = GameViewModel()
        viewModel.toggleCell(0, 0)
        val nextGen = nextGeneration(viewModel.workingCells)
        assert(!nextGen.contains(Pair(0, 0)))
    }

    @Test
    fun `four cells survive`() {
        val viewModel = GameViewModel()
        viewModel.toggleCell(0, 0)
        viewModel.toggleCell(1, 0)
        viewModel.toggleCell(0, 1)
        viewModel.toggleCell(1, 1)
        val nextGen = nextGeneration(viewModel.workingCells)
        assert(nextGen.contains(Pair(0, 0)))
        assert(nextGen.contains(Pair(0, 1)))
        assert(nextGen.contains(Pair(1, 0)))
        assert(nextGen.contains(Pair(1, 1)))
    }

    @Test
    fun `three cells multiply`() {
        val viewModel = GameViewModel()
        viewModel.toggleCell(0, 0)
        viewModel.toggleCell(0, 1)
        viewModel.toggleCell(1, 0)
        val nextGen = nextGeneration(viewModel.workingCells)
        assert(nextGen.contains(Pair(1, 1)))
    }
}