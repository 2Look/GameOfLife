package com.david.gameoflife.routing


import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.BackStackId
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController

internal val LocalNullableBackStack = compositionLocalOf<BackStack<Any>?> { null }
val LocalBackStack =
    compositionLocalOf<BackStack<Any>> { throw IllegalStateException("Missing Router(...) { ... } above") }

@Composable
inline fun <reified T : Any> Router(
    start: T,
    otherStart: List<T> = emptyList(),
    noinline children: @Composable BackStack<T>.(Route<T>) -> Unit
) =
    Router(T::class.java.name, start, otherStart, children)

@Composable
fun <T : Any> Router(
    id: BackStackId,
    start: T,
    otherStart: List<T> = emptyList(),
    children: @Composable BackStack<T>.(Route<T>) -> Unit
) {
    val activity = LocalContext.current as? ComponentActivity
    val parentKey = LocalNullableBackStack.current?.key
    val backStack = remember { backStackController.register(id, parentKey, start, otherStart) }
    var showRoutesState by remember { mutableStateOf(backStack.currentWithShowStack) }

    DisposableEffect(null, effect = {
        val onBackPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backStackController.pop()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(onBackPressCallback)

        backStackController.addListener(object : BackStackController.Listener {
            override fun onBackStackChanged(snapshot: List<GlobalRoute>) {
                onBackPressCallback.isEnabled = snapshot.size > 1
            }
        })

        val listener = object : BackStack.Listener<T> {
            override fun onCurrentChanged(route: Route<T>) {
                showRoutesState = backStack.currentWithShowStack
            }
        }
        backStack.addListener(listener)

        onDispose {
            backStack.removeListener(listener)
            onBackPressCallback.remove()
        }

    })


    @Suppress("UNCHECKED_CAST") val anyBackStack = backStack as BackStack<Any>
    CompositionLocalProvider(
        LocalBackStack.provides(anyBackStack),
        LocalNullableBackStack.provides(anyBackStack)
    ) {
        Box(modifier = Modifier) {
            showRoutesState.forEach { children(backStack, it) }
        }
    }
}