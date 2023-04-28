package com.fyp.pokedex.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fyp.pokedex.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home",
                fontSize = 32.sp,
                color = Color.Red
            )
            Spacer(modifier = Modifier.padding(vertical = 24.dp))
            Text(
                modifier = Modifier.clickable {
                    // "Navigate to detail s
                    navController.navigate(Screen.Detail.route)
                },
                text = "-->",
                fontSize = 32.sp,
                color = Color.DarkGray
            )
        }
    }

}