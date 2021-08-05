package com.david.gameoflife

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.gameoflife.persistance.AppDatabase
import com.david.gameoflife.persistance.Board
import com.david.gameoflife.routing.AppRoute
import com.david.gameoflife.routing.AppRoute.*
import com.david.gameoflife.routing.Router
import com.david.gameoflife.screens.BoardSelectScreen
import com.david.gameoflife.screens.GameScreen
import com.david.gameoflife.screens.MainMenuScreen
import com.david.gameoflife.ui.theme.GameOfLifeTheme
import com.david.gameoflife.utils.CellSet
import com.david.gameoflife.utils.Serialization.serialize
import com.david.gameoflife.utils.exhaustive
import com.koduok.compose.navigation.core.backStackController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val scheduler = Executors.newScheduledThreadPool(1)
    private var task: ScheduledFuture<*>? = null
    private var onPaused = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ComposeView(this)
        setContentView(view)
        view.setContent {
            GameOfLifeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppRoot()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backStackController.pop()) super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        task?.cancel(false)
        onPaused()
    }


    @Composable
    fun AppRoot() {
        val viewModel = viewModel<GameViewModel>()

        onPaused = {
            viewModel.gameIsRunning = false
            saveGameState(viewModel,this)
        }

        Router<AppRoute>(start = MainMenuRoute) { currentRoute ->
            when (currentRoute.value) {
                BoardSelectRoute -> BoardSelectScreen { push(GameRoute) }
                GameRoute -> GameScreen(
                    ::changeGameLoopSpeed,
                    ::scheduleGameLoop,
                    ::cancelTask,
                )
                MainMenuRoute -> MainMenuScreen({ push(GameRoute) }, { push(BoardSelectRoute) })
            }.exhaustive
        }
    }

    private fun cancelTask() {
        task?.cancel(false)
    }

    private fun changeGameLoopSpeed(
        gameIsRunning: Boolean,
        nextGen: (CellSet) -> CellSet,
        currentSpeedMultiplier: Long,
        updateCells: (CellSet) -> Unit,
        gameViewModel: GameViewModel
    ) {
        if (gameIsRunning) {
            cancelTask()
            scheduleGameLoop(nextGen, currentSpeedMultiplier, updateCells, gameViewModel)
        }
    }


    private fun scheduleGameLoop(
        nextGen: (CellSet) -> Set<Pair<Int, Int>>,
        currentSpeedMultiplier: Long,
        updateCells: (CellSet) -> Unit,
        gameViewModel: GameViewModel
    ) {

        task = scheduler.scheduleAtFixedRate(
            {
                val cellsCopy = gameViewModel.workingCells.toSet()
                val next = nextGen(cellsCopy)
                runBlocking {
                    withContext(Dispatchers.Main) {
                        updateCells(next)
                    }
                }
            },
            0,
            500 / currentSpeedMultiplier,
            TimeUnit.MILLISECONDS
        ) as ScheduledFuture<*>
    }

    companion object {

        fun saveGameState(
            gameViewModel: GameViewModel,
            currentContext: Context
        ) {
            gameViewModel.boardId?.let { id ->
                val db = AppDatabase.getInstance(currentContext)
                runBlocking {
                    db.boardDao().updateBoard(
                        Board(
                            id, gameViewModel.boardName, SimpleDateFormat.getDateInstance().format(
                                Date()
                            ),
                            gameViewModel.workingCells.serialize()
                        )
                    )
                }
            }
        }
    }

}

typealias GameLoopSpeedChanger = (Boolean, (CellSet) -> CellSet, Long, (CellSet) -> Unit, GameViewModel) -> Unit
typealias GameLoopScheduler = ((CellSet) -> CellSet, Long, (CellSet) -> Unit, GameViewModel) -> Unit
