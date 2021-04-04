package com.david.gameoflife.routing

sealed class AppRoute {
    object MainMenuRoute : AppRoute()
    object GameRoute : AppRoute()
    object BoardSelectRoute : AppRoute()
}
