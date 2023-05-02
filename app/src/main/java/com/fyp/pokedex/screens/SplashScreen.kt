package com.fyp.pokedex.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.fyp.pokedex.R
import com.fyp.pokedex.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.poke_loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(200.dp)
        )
    }
    LaunchedEffect(key1 = composition) {
        delay(3.seconds)
        navController.navigate(Screen.Home.route)
    }
}