package com.gominitta.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gominitta.android.presentation.detail.DetailScreen
import com.gominitta.android.presentation.home.HomeScreen

/**
 * Root navigation graph.
 *
 * This is the ONLY place that holds a reference to [NavHostController].
 * Feature screens are decoupled from navigation internals — they receive
 * plain lambda callbacks for any navigation action they need to trigger.
 *
 * Route strings come from [Routes]; never construct them inline here.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME,
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        modifier         = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { navController.navigate(Routes.DETAIL) },
            )
        }

        composable(Routes.DETAIL) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Add more composable(Routes.XXX) destinations here.
    }
}
