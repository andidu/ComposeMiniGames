package com.adorastudios.materialminigames

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adorastudios.materialminigames.presentation.Screens
import com.adorastudios.materialminigames.presentation.flappyCircle.FlappyCircleScreen
import com.adorastudios.materialminigames.presentation.mainScreen.MainScreen
import com.adorastudios.materialminigames.presentation.sideDote.SideDoteScreen
import com.adorastudios.materialminigames.ui.theme.MaterialMiniGamesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        var HEIGHT: Int = 0
            private set

        var WIDTH: Int = 0
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                getSystemService(WindowManager::class.java).currentWindowMetrics
            HEIGHT = metrics.bounds.height()
            WIDTH = metrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            HEIGHT = displayMetrics.heightPixels
            WIDTH = displayMetrics.widthPixels
        }

        setContent {
            MaterialMiniGamesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screens.MainScreen.route,
                    ) {
                        composable(
                            route = Screens.MainScreen.route,
                        ) {
                            MainScreen(navController = navController)
                        }
                        composable(
                            route = Screens.FlappyCircle.route,
                        ) {
                            FlappyCircleScreen()
                        }
                        composable(
                            route = Screens.SideDote.route,
                        ) {
                            SideDoteScreen()
                        }
                    }
                }
            }
        }
    }
}
