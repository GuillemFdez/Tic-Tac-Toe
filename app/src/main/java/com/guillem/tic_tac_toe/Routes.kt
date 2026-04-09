package com.guillem.tic_tac_toe

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Credits : Screen("credits")
    object Config : Screen("config")
    object Game : Screen("game/{mode}/{p1}/{p2}") {
        fun createRoute(mode: String, p1: String, p2: String) = "game/$mode/$p1/$p2"
    }
}