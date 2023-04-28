package com.fyp.pokedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Graph.Main) {
        mainNavGraph(navController)
    }
}


object Graph {
    const val ROOT = "root"
    const val Main = "main"
}