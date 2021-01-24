package com.david.gameoflife

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.ScaleObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.gesture.scaleGestureFilter
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import com.david.gameoflife.ui.theme.GameOfLifeTheme
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameOfLifeTheme(darkTheme = true) {
                Surface(color = MaterialTheme.colors.background) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
//    var board: Board by remember { mutableStateOf(Board()) }
    var dropDownExpanded: Boolean by remember { mutableStateOf(false) }
    var currentSpeed: Double by remember { mutableStateOf(1.0) }
    var gameIsRunning: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape
            ) {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(vectorResource(R.drawable.ic_baseline_save_24))
                }
                Spacer(Modifier.weight(0.5f, true))
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(vectorResource(R.drawable.ic_baseline_highlight_alt_24))
                }
                // The actions should be at the end of the BottomAppBar
                Spacer(Modifier.weight(1f, true))
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(Icons.Filled.Clear)
                }
                Spacer(Modifier.weight(0.5f, true))

                DropdownMenu(
                    toggle = {
                        TextButton(onClick = {
                            dropDownExpanded = dropDownExpanded.not()
                        }) {
                            Text("1X")
                        }
                    },
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = dropDownExpanded.not() }) {
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Text("1X", color = Color.White)
                    }
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Text("1.5X", color = Color.White)
                    }
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Text("2X", color = Color.White)
                    }
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Text("4X", color = Color.White)
                    }
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Text("8X", color = Color.White)
                    }
                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                gameIsRunning = gameIsRunning.not()
            }) {
                if (!gameIsRunning)
                    Icon(Icons.Filled.PlayArrow)
                else Icon(vectorResource(R.drawable.ic_baseline_pause_24))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bodyContent = {
            GameGrid()
        }
    )

}

@Composable
private fun GameGrid(gameViewModel: GameViewModel = viewModel()) {
    var currentScalingFactor: Float by remember { mutableStateOf(1.0f) }
    var currentTranslation: Offset by remember { mutableStateOf(Offset.Zero) }
    var zoomPivot: Offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var increment: Int by remember { mutableStateOf(0) }

    Canvas(modifier = Modifier.fillMaxSize().scaleGestureFilter(object : ScaleObserver {

        override fun onScale(scaleFactor: Float) {
            currentTranslation +=
                (zoomPivot) / (currentScalingFactor * scaleFactor) - (zoomPivot) / currentScalingFactor
            currentScalingFactor *= scaleFactor
        }
    }).dragGestureFilter(object : DragObserver {

        override fun onDrag(dragDistance: Offset): Offset {
            currentTranslation += dragDistance / currentScalingFactor
            Log.d("TRANSLATION", "$currentTranslation")
            return dragDistance
        }

    }).tapGestureFilter {
        val tapPosition = it / currentScalingFactor - currentTranslation
        gameViewModel.toggleCell(
            tapPosition.y.toInt() / increment,
            tapPosition.x.toInt() / increment
        )
    }, onDraw = {
        val (width, height) = size
        withTransform({
            scale(currentScalingFactor, pivot = Offset(0.0f, 0.0f))
            translate(currentTranslation.x, currentTranslation.y)
        }) {
            zoomPivot = Offset(width / 2, height / 2)
            increment = max(width, height).toInt() / 100
            for (i in 0 until width.toInt() step increment) {
                drawLine(
                    Color.LightGray,
                    start = Offset(i.toFloat(), 0.0f),
                    Offset(i.toFloat(), height)
                )
            }
            for (i in 0 until height.toInt() step increment) {
                drawLine(
                    Color.LightGray,
                    start = Offset(0.0f, i.toFloat()),
                    Offset(width, i.toFloat())
                )

            }

            val board = gameViewModel.board
            for (i in board.matrix.indices) {
                for (j in board.matrix[0].indices) {
                    when (board.matrix[i][j]) {
                        Cell.Alive -> {
                            drawRect(
                                color = Color.LightGray,
                                topLeft = Offset(j * increment.toFloat(), i * increment.toFloat()),
                                size = Size(increment.toFloat(), increment.toFloat())
                            )
                        }
                        else -> Unit
                    }
                }
            }

        }


    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GameOfLifeTheme {
        GameScreen()
    }
}