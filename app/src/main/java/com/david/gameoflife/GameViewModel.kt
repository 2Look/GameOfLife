package com.david.gameoflife

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.david.gameoflife.screens.ConstructData
import com.david.gameoflife.utils.CellSet

sealed class CanvasMode {
    object Navigation : CanvasMode()
    object Selection : CanvasMode()
}


class GameViewModel : ViewModel() {

    var id: Int? = null
    var name: String = "Default"

    var constructs: List<ConstructData> by mutableStateOf(emptyList())

    var canvasMode: CanvasMode by mutableStateOf(CanvasMode.Navigation)

    var gameIsRunning: Boolean by mutableStateOf(false)

    var displayedCells: Set<Pair<Int, Int>> by mutableStateOf(emptySet())
        private set

    var workingCells = setOf<Pair<Int, Int>>()
        private set

    fun updateCells(newCells: CellSet) {
        displayedCells = newCells
        workingCells = newCells.toSet()
    }

    fun toggleCell(x: Int, y: Int) {
        val pair = Pair(x, y)
        displayedCells = if (!displayedCells.contains(pair)) {
            displayedCells + pair
        } else {
            displayedCells - pair
        }
        workingCells = displayedCells.toSet()
    }


    fun clearCells() {
        displayedCells = emptySet()
        workingCells = emptySet()
    }


}


