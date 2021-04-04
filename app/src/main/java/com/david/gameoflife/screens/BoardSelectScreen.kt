package com.david.gameoflife.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.gameoflife.ConfirmDialog
import com.david.gameoflife.GameViewModel
import com.david.gameoflife.R
import com.david.gameoflife.persistance.AppDatabase
import com.david.gameoflife.persistance.Board
import com.david.gameoflife.ui.theme.red700
import com.david.gameoflife.utils.CellSet
import com.david.gameoflife.utils.Serialization.parseCoordinates
import kotlinx.coroutines.launch
import java.text.DateFormat.MEDIUM
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BoardSelectScreen(navigateToGameScreen: () -> Unit) {
    val db = AppDatabase.getInstance(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    var boards: List<BoardInfoData> by remember { mutableStateOf(emptyList()) }
    var selectedId by remember { mutableStateOf(-1) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (boards.isEmpty() && selectedId != -1)
        Text(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            text = "You have no saved worlds!",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

    if (showDeleteDialog)
        ConfirmDialog(
            question = "Are you sure you want to delete this world?",
            onConfirm = {
                coroutineScope.launch {
                    db.boardDao().deleteBoard(Board(selectedId, "", "", ""))
                    boards = boards.filter { boardInfoData -> boardInfoData.id != selectedId }
                    showDeleteDialog = false
                }
            },
            onCancel = { showDeleteDialog = false })

    DisposableEffect(key1 = null, effect = {
        coroutineScope.launch {
            val dbBoards = db.boardDao().getAll()
            boards = dbBoards.map {
                BoardInfoData(
                    it.name,
                    it.boardId,
                    SimpleDateFormat.getDateInstance().parse(it.date)!!,
                    it.coordinates.parseCoordinates()
                )
            }
        }
        onDispose { }
    })
    LazyColumn {
        for (board in boards) {
            item {
                BoardInfo(board, navigateToGameScreen) { id ->
                    selectedId = id
                    showDeleteDialog = true
                }
            }
        }
    }
}

data class BoardInfoData(val name: String, val id: Int, val lastModified: Date, val cells: CellSet)

@Composable
fun BoardInfo(
    boardInfoData: BoardInfoData,
    navigateToGameScreen: () -> Unit,
    trashCanPressed: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val gameViewModel = viewModel<GameViewModel>()

    Card(Modifier.fillMaxWidth().padding(PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp))) {
        Row {
            Column(Modifier.padding(8.dp)) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = boardInfoData.name,
                    fontSize = 20.sp

                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = SimpleDateFormat.getDateInstance(MEDIUM)
                        .format(boardInfoData.lastModified),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                onClick = {
                    trashCanPressed(boardInfoData.id)
                }) {
                Icon(painterResource(R.drawable.ic_baseline_delete_24), "Delete", tint = red700)
            }
            Button(
                modifier = Modifier.padding(PaddingValues(8.dp, 4.dp))
                    .align(Alignment.CenterVertically),
                onClick = {
                    coroutineScope.launch {
                        gameViewModel.updateCells(boardInfoData.cells)
                        gameViewModel.name = boardInfoData.name
                        gameViewModel.id = boardInfoData.id
                        navigateToGameScreen()
                    }
                }) {
                Icon(Icons.Default.PlayArrow, "Play")
            }
        }
    }
}
