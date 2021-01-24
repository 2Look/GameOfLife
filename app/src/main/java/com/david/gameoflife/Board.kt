package com.david.gameoflife

import androidx.compose.runtime.mutableStateOf

class Board(private val maxBoardWidth: Int = 100, private val maxBoardHeight: Int = 100) {

    var updateToggle = mutableStateOf(false)
    private set

    var matrix = deadBoard()
        private set

    private fun deadBoard() = generateSequence {
        generateSequence { Cell.Dead }.take(maxBoardWidth).toArray<Cell>(maxBoardWidth)
    }.take(maxBoardHeight).toArray(maxBoardHeight)

    fun toggleCell(x: Int, y: Int): Board {
        matrix[x][y] = when (matrix[x][y]) {
            Cell.Alive -> Cell.Dead
            Cell.Dead -> Cell.Alive
        }
        return this
        updateToggle.value = !updateToggle.value
    }

    private fun livingNeighboursForCell(x: Int, y: Int): Int {
        var counter = 0
        // down, up, right, left
        if (x > 0 && matrix[x - 1][y].isAlive()) counter += 1
        if (x < maxBoardHeight - 1 && matrix[x + 1][y].isAlive()) counter += 1
        if (y > 0 && matrix[x][y - 1].isAlive()) counter += 1
        if (y < maxBoardWidth - 1 && matrix[x][y + 1].isAlive()) counter += 1

        //diagonals
        if (x > 0 && y > 0 && matrix[x - 1][y - 1].isAlive()) counter += 1
        if (x < maxBoardHeight - 1 && y < maxBoardWidth - 1 && matrix[x + 1][y + 1].isAlive()) counter += 1
        if (x < maxBoardHeight - 1 && y > 0 && matrix[x + 1][y - 1].isAlive()) counter += 1
        if (x > 0 && y < maxBoardWidth - 1 && matrix[x - 1][y + 1].isAlive()) counter += 1
        return counter

    }

    fun nextGeneration() {
        for (i in 0 until maxBoardHeight) {
            for (j in 0 until maxBoardWidth) {
                val currentCell = matrix[i][j]
                val neighbourCount = livingNeighboursForCell(i, j)
                matrix[i][j] = when (currentCell) {
                    // Any live cell with two or three live neighbours survives.
                    Cell.Alive ->
                        if (neighbourCount == 2 || neighbourCount == 3) Cell.Alive else Cell.Dead
                    // Any dead cell with three live neighbours becomes a live cell.
                    Cell.Dead -> if (livingNeighboursForCell(i, j) == 3) Cell.Alive else Cell.Dead
                    // All other live cells die in the next generation. Similarly, all other dead cells stay dead.
                }
            }
        }
        updateToggle.value = !updateToggle.value
    }
}
