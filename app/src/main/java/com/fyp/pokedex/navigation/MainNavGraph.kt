package com.fyp.pokedex.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fyp.pokedex.screens.DetailScreen
import com.fyp.pokedex.screens.HomeScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.Main,
        startDestination = Screen.Home.route
    ) {
        // ADD MAIN SCREENS
        composable(route = Screen.Home.route) { HomeScreen(navController) }
        composable(route = Screen.Detail.route) { DetailScreen(navController) }
    }
}