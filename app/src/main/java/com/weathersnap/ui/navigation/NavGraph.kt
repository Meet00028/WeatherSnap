package com.weathersnap.ui.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.weathersnap.ui.camera.CameraScreen
import com.weathersnap.ui.createreport.CreateReportScreen
import com.weathersnap.ui.reports.ReportsScreen
import com.weathersnap.ui.weather.WeatherScreen

object Routes {
    const val Weather = "weather"
    const val CreateReport = "create_report/{weatherJson}"
    const val Camera = "camera"
    const val Reports = "reports"

    fun createReport(weatherJson: String): String = "create_report/${Uri.encode(weatherJson)}"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Weather,
    ) {
        composable(
            route = Routes.Weather,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { it }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { -it }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { it }
            },
        ) {
            WeatherScreen(
                navController = navController,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Routes.CreateReport,
            arguments = listOf(navArgument("weatherJson") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { it }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { -it }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { it }
            },
        ) {
            CreateReportScreen(
                navController = navController,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Routes.Camera,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { it }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { -it }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { it }
            },
        ) {
            CameraScreen(
                navController = navController,
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Routes.Reports,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { it }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { -it }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(280)) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(280)) { it }
            },
        ) {
            ReportsScreen(
                navController = navController,
                viewModel = hiltViewModel(),
            )
        }
    }
}
