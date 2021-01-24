package com.david.gameoflife

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var board: Board by mutableStateOf(Board())
        private set

    fun toggleCell(x: Int, y: Int) {
        board = board.toggleCell(x, y)
    }
}