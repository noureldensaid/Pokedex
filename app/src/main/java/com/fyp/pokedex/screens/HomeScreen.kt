package com.fyp.pokedex.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.fyp.pokedex.ui.theme.poky

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = MaterialTheme.colors.background
    ) {
        var hideKeyboard by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { hideKeyboard = true },
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                modifier = Modifier.padding(16.dp),
                hint = "Search..",
                icon = Icons.Filled.Search,
                hideKeyboard = hideKeyboard,
                onFocusClear = {
                    hideKeyboard = false
                    isSearchActive = false
                },
            ) {
                searchQuery = it
                isSearchActive = true
                viewModel.getPokemonInfo(searchQuery)
            }
            PokemonList(navController, isSearchActive, searchQuery)
        }

    }
}


@Composable
fun PokemonList(
    navController: NavHostController,
    isSearchActive: Boolean = false,
    query: String,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    var data = viewModel.pokemonList
    if (isSearchActive) {
        data = listOf(viewModel.pokemonSearchResults.value)
    }
    val endReached = viewModel.endReached
    val loadError = viewModel.loadError
    val isLoading = viewModel.isLoading

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        val itemCount = data.size
        items(itemCount) { index ->
            PokemonItem(entry = data[index], navController = navController)
            if (index >= itemCount - 1 && !endReached && !isLoading) {
                viewModel.getPokemonList()
            }
        }
    }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading && !isSearchActive) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.getPokemonList()
            }
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.getPokemonInfo(query)
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
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    // theme color
    val defaultDominantColor = MaterialTheme.colors.background
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
                    .clip(RoundedCornerShape(50.dp))
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.pokemonName,
                fontSize = 20.sp,
                fontFamily = poky,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                color = MaterialTheme.colors.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search..",
    icon: ImageVector = Icons.Filled.Search,
    hideKeyboard: Boolean = false,
    onFocusClear: () -> Unit = {},
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(32.dp))
            .background(MaterialTheme.colors.surface, RoundedCornerShape(32.dp))
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            value = searchText,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = "null",
                )
            },
            onValueChange = { searchText = it },
            // Use the hint text as the placeholder for the TextField
            placeholder = {
                Text(
                    text = hint,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 20.sp,
                    fontFamily = poky,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Light,
                )
            },
            singleLine = true,
            // Set the text color of the TextField to black
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onBackground,
                fontSize = 20.sp,
                fontFamily = poky,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Light,
            ),
            // Use transparent colors for the TextField background and indicators
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                onSearch(searchText)
            }),
        )
    }
    if (hideKeyboard) {
        focusManager.clearFocus()
        // Call onFocusClear to reset hideKeyboard state to false
        onFocusClear()
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

