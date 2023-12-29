package com.adorastudios.materialminigames.presentation

sealed class Screens(val route: String) {
    object MainScreen : Screens("main_screen")
    object FlappyCircle : Screens("flappy_circle")
}
