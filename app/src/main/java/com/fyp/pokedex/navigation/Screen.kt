package com.fyp.pokedex.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail")
    // pass args ext function
    // works only with mandatory args
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
