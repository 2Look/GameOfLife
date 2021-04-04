package com.david.gameoflife.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.gameoflife.GameViewModel
import com.david.gameoflife.TextBoxDialog
import com.david.gameoflife.persistance.AppDatabase
import com.david.gameoflife.persistance.Board
import com.david.gameoflife.utils.Serialization.serialize
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainMenuScreen(
    navigateToNewBoard: () -> Unit,
    navigateToBoardSelector: () -> Unit
) {
    val gameViewModel = viewModel<GameViewModel>()
    var showSaveDialog: Boolean by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentContext = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(currentContext)

    DisposableEffect(null, effect = {
        gameViewModel.clearCells()
        gameViewModel.id = null
        onDispose {

        }
    })



    if (showSaveDialog) {
        TextBoxDialog(
            placeholderText = "New World Name",
            onDismissRequest = { showSaveDialog = false },
            onCancel = { showSaveDialog = false },
            onConfirm = {
                coroutineScope.launch {
                    val boardId = db.boardDao().insertBoard(
                        Board(
                            0,
                            it,
                            SimpleDateFormat.getDateInstance().format(Date()),
                            gameViewModel.workingCells.serialize()
                        )
                    )
                    showSaveDialog = false
                    gameViewModel.name = it
                    gameViewModel.id = boardId.toInt()
                    navigateToNewBoard()
                }
            })
    }

    GameGrid(CanvasMode.Navigation, interactive = false)
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp),
            onClick = {
                showSaveDialog = true
            }
        ) {
            Text("New Game")
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp),
            onClick = {
                gameViewModel.clearCells()
                navigateToBoardSelector()
            }) {
            Text("Load Board")
        }


    }
}