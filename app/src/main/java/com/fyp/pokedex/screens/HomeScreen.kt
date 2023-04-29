package com.fyp.pokedex.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fyp.pokedex.R
import com.fyp.pokedex.models.pokemonList.PokedexListEntry
import com.fyp.pokedex.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<HomeViewModel>()
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
            PokemonList(navController)
        }

    }
}


@Composable
fun PokemonList(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading = remember { viewModel.isLoading }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        val itemCount = pokemonList.size
        items(itemCount) { index ->
            PokemonItem(entry = pokemonList[index], navController = navController)

            if (index >= itemCount - 1 && !endReached) {
                viewModel.getPokemonList()
            }
        }
    }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.getPokemonList()
            }
        }
    }

}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun PokemonItem(

    entry: PokedexListEntry,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // theme color
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember { mutableStateOf(defaultDominantColor) }


    Card(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // image
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(10.dp, RoundedCornerShape(300.dp))
                    .clip(RoundedCornerShape(300.dp))
                    .aspectRatio(1f)
                    .background(Brush.verticalGradient(listOf(dominantColor, defaultDominantColor)))
                    .clickable {
                        navController.navigate(
                            Screen.Detail.withArgs(
                                "${dominantColor.toArgb()}", entry.pokemonName
                            )
                        )
                    }
                    .padding(8.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                onSuccess = { success ->
                    val drawable = success.result.drawable
                    viewModel.calculateDominateColor(drawable) { color ->
                        dominantColor = color
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = entry.pokemonName,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
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
fun PokemonItemPreview() {
    PokemonItem(
        entry = PokedexListEntry("moo", "", 1),
        navController = rememberNavController()
    )
}

