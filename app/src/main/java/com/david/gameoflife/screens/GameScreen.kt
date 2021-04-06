package com.david.gameoflife.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.gameoflife.ConfirmDialog
import com.david.gameoflife.GameLoopScheduler
import com.david.gameoflife.GameLoopSpeedChanger
import com.david.gameoflife.GameViewModel
import com.david.gameoflife.MainActivity.Companion.saveGameState
import com.david.gameoflife.R
import com.david.gameoflife.TextBoxDialog
import com.david.gameoflife.persistance.AppDatabase
import com.david.gameoflife.persistance.Construct
import com.david.gameoflife.ui.theme.purple700
import com.david.gameoflife.ui.theme.teal200
import com.david.gameoflife.utils.Cell
import com.david.gameoflife.utils.CellSet
import com.david.gameoflife.utils.GameUtils
import com.david.gameoflife.utils.GameUtils.checkCell
import com.david.gameoflife.utils.Serialization.parseCoordinates
import com.david.gameoflife.utils.Serialization.serialize
import com.david.gameoflife.utils.normalize
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.pow

sealed class CanvasMode {
    object Navigation : CanvasMode()
    object Selection : CanvasMode()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameScreen(
    changeGameLoopSpeed: GameLoopSpeedChanger,
    scheduleGameLoop: GameLoopScheduler,
    cancelTask: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    var dropDownExpanded: Boolean by remember { mutableStateOf(false) }
    var currentSpeedMultiplier: Long by remember { mutableStateOf(1) }
    var showClearDialog: Boolean by remember { mutableStateOf(false) }
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    var canvasMode: CanvasMode by remember { mutableStateOf(CanvasMode.Navigation) }
    val coroutineScope = rememberCoroutineScope()
    val currentContext = LocalContext.current
    val gameIsRunning = gameViewModel.gameIsRunning
    val nextGen = GameUtils::nextGeneration
    val updateCells = gameViewModel::updateCells


    DisposableEffect(null, effect = {
        onDispose {
            saveGameState(gameViewModel, currentContext)
        }
    })

    if (showClearDialog) {
        ConfirmDialog(
            question = "Are you sure you want to clear the board",
            onConfirm = {
                gameViewModel.clearCells()
                showClearDialog = false
            },
            onCancel = {
                showClearDialog = false
            })
    }


    BottomDrawer(
        gesturesEnabled = canvasMode == CanvasMode.Navigation,
        drawerState = drawerState,
        drawerContent = {
            ConstructList()

        }) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    cutoutShape = CircleShape
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(painterResource(R.drawable.ic_baseline_dashboard_24), "View Saved")
                    }
                    Spacer(Modifier.weight(0.5f, true))
                    IconButton(onClick = {
                        canvasMode = if (canvasMode == CanvasMode.Navigation) CanvasMode.Selection
                        else CanvasMode.Navigation
                    }) {
                        if (canvasMode == CanvasMode.Selection)
                            Icon(
                                painterResource(R.drawable.ic_baseline_highlight_alt_24),
                                "View Saved",
                                tint = purple700
                            )
                        else
                            Icon(
                                painterResource(R.drawable.ic_baseline_highlight_alt_24),
                                "Select Shape"
                            )
                    }
                    Spacer(Modifier.weight(1f, true))

                    IconButton(onClick = {
                        showClearDialog = true
                    }) {
                        Icon(Icons.Filled.Clear, "Clear Board")
                    }

                    Spacer(Modifier.weight(0.5f, true))

                    Box {
                        TextButton(onClick = {
                            dropDownExpanded = dropDownExpanded.not()
                        }) {
                            Text("${currentSpeedMultiplier}X")
                        }
                        DropdownMenu(
                            expanded = dropDownExpanded,
                            onDismissRequest = { dropDownExpanded = dropDownExpanded.not() }) {
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 1
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("1X", color = Color.White)
                            }
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 2
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("2X", color = Color.White)
                            }
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 4
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("4X", color = Color.White)
                            }
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 8
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("8X", color = Color.White)
                            }
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 16
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("16X", color = Color.White)
                            }
                            DropdownMenuItem(onClick = {
                                currentSpeedMultiplier = 128
                                changeGameLoopSpeed(
                                    gameIsRunning,
                                    nextGen,
                                    currentSpeedMultiplier,
                                    updateCells,
                                    gameViewModel
                                )
                                dropDownExpanded = dropDownExpanded.not()
                            }) {
                                Text("128X", color = Color.White)
                            }
                        }

                    }

                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (!gameIsRunning) {
                        scheduleGameLoop(
                            nextGen,
                            currentSpeedMultiplier,
                            updateCells,
                            gameViewModel
                        )
                    } else {
                        cancelTask()
                    }
                    gameViewModel.gameIsRunning = gameIsRunning.not()
                }) {
                    if (gameIsRunning) {
                        Icon(painterResource(R.drawable.ic_baseline_pause_24), "Pause")
                    } else {
                        Icon(Icons.Filled.PlayArrow, "Play")
                    }

                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
        ) {
            GameGrid(canvasMode)
        }

    }


}

data class ConstructData(val id: Int, val name: String, val cells: CellSet)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConstructList() {
    var constructs: List<ConstructData> by remember { mutableStateOf(emptyList()) }
    var showDeletionDialog: Boolean by remember { mutableStateOf(false) }
    var constructToDelete by remember { mutableStateOf(0 to "") }
    val coroutineScope = rememberCoroutineScope()
    val currentContext = LocalContext.current
    val db = AppDatabase.getInstance(currentContext)

    if (showDeletionDialog) {
        ConfirmDialog(
            question = "Are you sure you want to delete ${constructToDelete.second}",
            onConfirm = {
                coroutineScope.launch {
                    db.constructDao().deleteConstruct(
                        Construct(
                            constructToDelete.first,
                            constructToDelete.second,
                            ""
                        )
                    )
                    showDeletionDialog = false
                }
            },
            onCancel = {
                constructToDelete = 0 to ""
                showDeletionDialog = false
            })
    }

    SideEffect(effect = {
        coroutineScope.launch {
            constructs = db.constructDao().getAll().map {
                ConstructData(
                    it.uid,
                    it.name,
                    it.coordinates.parseCoordinates()
                )
            }
        }
    })


    LazyVerticalGrid(cells = GridCells.Adaptive(128.dp), content = {
        constructs.forEach { construct ->
            item {
                ConstructInfo(construct, onTap = {
                    constructToDelete = construct.id to construct.name
                    showDeletionDialog = true
                }, onLongPress = {})
            }
        }
    })

}

@Composable
fun ConstructInfo(
    construct: ConstructData,
    onTap: (Offset) -> Unit,
    onLongPress: (Offset) -> Unit
) {
    Card(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .shadow(8.dp),
        backgroundColor = purple700
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = onTap,
                        onLongPress = onLongPress
                    )
                }
        ) {
            Text(construct.name)
            Canvas(modifier = Modifier
                .size(100.dp)
                .padding(top = 32.dp)
                .background(Color.Red), onDraw = {
                drawCells(
                    construct.cells,
                    size.height.toInt() / 2,
                    Offset.Zero,
                    size.height,
                    size.width
                )
            })
        }
    }
}

@Composable
fun GameGrid(
    canvasMode: CanvasMode,
    subdivisions: Int = 50,
    interactive: Boolean = true,
    gameViewModel: GameViewModel = viewModel()
) {
    var currentScalingFactor: Float by remember { mutableStateOf(1f) }
    var currentTranslation: Offset by remember {
        mutableStateOf(
            Offset.Zero
        )
    }
    var zoomPivot: Offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var increment: Int by remember { mutableStateOf(0) }
    var gridOpacity: Float by remember { mutableStateOf(1.0f) }
    val toggleCell = gameViewModel::toggleCell

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                if (!interactive) return@detectTransformGestures
                val newScalingFactor = currentScalingFactor * zoom
                currentTranslation +=
                    (zoomPivot) / newScalingFactor - (zoomPivot) / currentScalingFactor
                currentScalingFactor = newScalingFactor
                gridOpacity = currentScalingFactor.pow(3)
                gridOpacity = minOf(gridOpacity, 1.0f)

                val newTranslation = currentTranslation + pan / currentScalingFactor
                currentTranslation = newTranslation
            }
        }
        .pointerInput(Unit) {

            detectTapGestures(onTap = {
                if (!interactive) return@detectTapGestures
                val tapPosition = it / currentScalingFactor - currentTranslation
                val actualX = if (tapPosition.x < 0) tapPosition.x - increment else tapPosition.x
                val actualY = if (tapPosition.y < 0) tapPosition.y - increment else tapPosition.y
                toggleCell(
                    actualX.toInt() / increment,
                    actualY.toInt() / increment
                )
            })
        }, onDraw = {
        val (width, height) = size
        withTransform({
            scale(currentScalingFactor, pivot = Offset(0.0f, 0.0f))
            translate(currentTranslation.x, currentTranslation.y)
        }) {
            zoomPivot = Offset(width / 2, height / 2)
            increment = height.toInt() / subdivisions


            val viewPort = -currentTranslation
            val x = viewPort.x.toInt()
            val y = viewPort.y.toInt()
            val unscaledWidth = width / currentScalingFactor
            val unscaledHeight = height / currentScalingFactor

            drawCells(
                gameViewModel.displayedCells,
                increment,
                viewPort,
                unscaledHeight,
                unscaledWidth
            )

            if (gridOpacity <= 0.02f) return@Canvas
            for (i in x - x % increment until unscaledWidth.toInt() + x step increment) {
                drawLine(
                    Color.DarkGray,
                    start = Offset(i.toFloat(), y.toFloat()),
                    Offset(i.toFloat(), unscaledHeight + y.toFloat()),
                    alpha = gridOpacity
                )
            }
            for (i in y - y % increment until unscaledHeight.toInt() + y step increment) {
                drawLine(
                    Color.DarkGray,
                    start = Offset(x.toFloat(), i.toFloat()),
                    Offset(unscaledWidth + x.toFloat(), i.toFloat()),
                    alpha = gridOpacity
                )

            }
        }
    })
    if (canvasMode == CanvasMode.Selection)
        SelectionCanvas(increment, currentScalingFactor, currentTranslation)
}

@Composable
fun SelectionCanvas(increment: Int, currentScalingFactor: Float, currentTranslation: Offset) {
    var selectionStart: Offset by remember { mutableStateOf(Offset.Zero) }
    var selectionDistance: Offset by remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()
    val gameViewModel = viewModel<GameViewModel>()
    var showSaveDialog: Boolean by remember { mutableStateOf(false) }
    var selectedCells: CellSet by remember { mutableStateOf(emptySet()) }
    val currentContext = LocalContext.current

    if (showSaveDialog) {
        TextBoxDialog(
            placeholderText = "New Construct Name",
            confirmText = "Save",
            onDismissRequest = {
                selectedCells = emptySet()
                showSaveDialog = false
            },
            onCancel = {
                selectedCells = emptySet()
                showSaveDialog = false
            },
            onConfirm = {
                if (selectedCells.isEmpty()) return@TextBoxDialog
                coroutineScope.launch {
                    val db = AppDatabase.getInstance(currentContext)
                    db.constructDao()
                        .insertConstruct(
                            Construct(
                                0,
                                it,
                                selectedCells.normalize().serialize()
                            )
                        )
                    selectedCells = emptySet()
                    showSaveDialog = false
                }
            })
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onPress = {
                selectionStart = it
            })
        }
        .pointerInput(Unit) {

            detectDragGestures(
                onDrag = { pointerInputChange: PointerInputChange, dragAmount: Offset ->
                    selectionDistance += dragAmount
                    pointerInputChange.consumed.positionChange = true

                },
                onDragEnd = {
                    val sStart = selectionStart / currentScalingFactor - currentTranslation
                    val sStartX = if (sStart.x < 0) sStart.x - increment else sStart.x
                    val sStartY = if (sStart.y < 0) sStart.y - increment else sStart.y
                    val startCell = Pair(
                        sStartX.toInt() / increment,
                        sStartY.toInt() / increment
                    )
                    val selectionEnd = selectionStart + selectionDistance
                    val sEnd = selectionEnd / currentScalingFactor - currentTranslation
                    val sEndX = if (sStart.x < 0) sEnd.x - increment else sEnd.x
                    val sEndY = if (sStart.y < 0) sEnd.y - increment else sEnd.y
                    val endCell = Pair(
                        sEndX.toInt() / increment,
                        sEndY.toInt() / increment
                    )

                    selectedCells = gameViewModel.workingCells
                        .filter {
                            it.first in startCell.first..endCell.first
                                    && it.second in startCell.second..endCell.second
                        }
                        .toSet()

                    showSaveDialog = true

                    selectionDistance = Offset.Zero
                    selectionStart = Offset.Zero
                }
            )
        }, onDraw = {
        if (selectionDistance != Offset.Zero) {
            drawRect(
                teal200,
                selectionStart,
                Size(selectionDistance.x, selectionDistance.y),
                style = Stroke()
            )
        }
    })
}

private fun DrawScope.drawCells(
    cells: CellSet,
    increment: Int,
    viewPort: Offset,
    unscaledHeight: Float,
    unscaledWidth: Float
) {
    cells.forEach { (i, j) ->
        val lowerX = viewPort.x - increment
        val lowerY = viewPort.y - increment
        val upperX = viewPort.x + unscaledWidth
        val upperY = viewPort.y + unscaledHeight
        if (i.toFloat() * increment in lowerX..upperX && j.toFloat() * increment in lowerY..upperY)
            when (cells.checkCell(i, j)) {
                Cell.Alive -> {
                    drawRect(
                        color = Color.LightGray,
                        topLeft = Offset(
                            i * increment.toFloat(),
                            j * increment.toFloat()
                        ),
                        size = Size(increment.toFloat(), increment.toFloat())
                    )
                }
                Cell.Dead -> Unit
            }
    }
}