package com.fyp.pokedex.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fyp.pokedex.R

@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(modifier = Modifier.padding(16.dp)) {
                // Call the onSearch from view model
            }
        }
    }

}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search..",
    icon: ImageVector = Icons.Filled.Search,
    onSearch: (String) -> Unit
) {
    // This state variable holds the current search text entered by the user
    var searchText by remember { mutableStateOf("") }

    // The Box composable is used to hold the icon and TextField components
    Box(
        modifier = modifier
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(32.dp))
            .background(Color.White, RoundedCornerShape(32.dp))
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // The Icon composable displays the search icon on the left side of the search bar
        Icon(
            imageVector = icon,
            contentDescription = "Search",
            modifier = Modifier.padding(start = 16.dp)
        )
        // The TextField composable is used to allow the user to enter search text
        TextField(
            modifier = Modifier
                // Fill the available width of the Box with the TextField
                .fillMaxWidth()
                // Add some padding to the left of the TextField to make room for the icon
                .padding(start = 32.dp),

            // Bind the value of the TextField to the searchText state variable
            value = searchText,
            // Update the searchText state variable as the user types
            onValueChange = {
                searchText = it
                // Call the onSearch lambda with the current search text whenever it changes
                onSearch(it)
            },
            // Use the hint text as the placeholder for the TextField
            placeholder = { Text(hint) },
            // Ensure that the TextField only allows a single line of text
            singleLine = true,
            // Set the text color of the TextField to black
            textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
            // Use transparent colors for the TextField background and indicators
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}


//@Preview(showBackground = true)
//@Composable
//fun SearchBarPreview() {
//    SearchBar(modifier = Modifier.fillMaxSize())
//}