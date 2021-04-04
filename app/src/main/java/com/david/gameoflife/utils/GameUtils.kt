package com.david.gameoflife.utils

typealias CellSet = Set<Pair<Int, Int>>

object GameUtils {
    fun neighboursFor(x: Int, y: Int) =
        setOf(
            Pair(x - 1, y),
            Pair(x, y - 1),
            Pair(x + 1, y),
            Pair(x, y + 1),
            Pair(x - 1, y - 1),
            Pair(x - 1, y + 1),
            Pair(x + 1, y - 1),
            Pair(x + 1, y + 1)
        )

    fun CellSet.checkCell(x: Int, y: Int): Cell {
        val cells = this
        val pair = Pair(x, y)
        return if (cells.contains(pair)) Cell.Alive else Cell.Dead
    }

    private fun CellSet.countLivingNeighboursForCell(x: Int, y: Int): Int {
        val cells = this
        var counter = 0
        // down, up, right, left
        if (cells.contains(Pair(x - 1, y))) counter += 1
        if (cells.contains(Pair(x, y - 1))) counter += 1
        if (cells.contains(Pair(x + 1, y))) counter += 1
        if (cells.contains(Pair(x, y + 1))) counter += 1

        //diagonals
        if (cells.contains(Pair(x - 1, y - 1))) counter += 1
        if (cells.contains(Pair(x - 1, y + 1))) counter += 1
        if (cells.contains(Pair(x + 1, y - 1))) counter += 1
        if (cells.contains(Pair(x + 1, y + 1))) counter += 1
        return counter

    }

    fun nextGeneration(cells: CellSet): CellSet {
        val candidates = cells.potentialCandidates()
        val nextGen = mutableSetOf<Pair<Int, Int>>()

        candidates.forEach { (i, j) ->
            val currentCell = cells.checkCell(i, j)
            val neighbourCount = cells.countLivingNeighboursForCell(i, j)
            // Any live cell with two or three live neighbours survives.
            if (currentCell == Cell.Alive && (neighbourCount == 2 || neighbourCount == 3))
                nextGen.add(Pair(i, j))
            // Any dead cell with three live neighbours becomes a live cell.
            if (currentCell == Cell.Dead && neighbourCount == 3) nextGen.add(Pair(i, j))
            // All other live cells die in the next generation. Similarly, all other dead cells stay dead.
        }
        return nextGen
    }

}
