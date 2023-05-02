package com.fyp.pokedex.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fyp.pokedex.R
import com.fyp.pokedex.models.pokemon.Pokemon
import com.fyp.pokedex.models.pokemon.Type
import com.fyp.pokedex.ui.theme.poky
import com.fyp.pokedex.util.Resource
import com.fyp.pokedex.util.parseStatToAbbr
import com.fyp.pokedex.util.parseStatToColor
import com.fyp.pokedex.util.parseTypeToColor
import java.util.*

@Composable
fun DetailScreen(
    navController: NavHostController,
    dominantColor: Color,
    pokemonName: String,
    viewModel: DetailScreenViewModel = hiltViewModel()

) {
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName.lowercase())
    }.value


    // make box which contains
    // top section -> pokemon details states --> pokemon image

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)

    ) {
        // top section
        PokemonDetailTopSection(
            navController = navController,
            dominantColor = dominantColor,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            pokemonInfo = pokemonInfo,
        )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .padding(
                    top = 250.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = 200.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
        )
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavHostController,
    dominantColor: Color,
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.background, dominantColor
                    )
                )
            )
    ) {

        Icon(
            modifier = Modifier
                .size(36.dp)
                .offset(24.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                },
            painter = painterResource(R.drawable.ic_back_arrow),
            tint = MaterialTheme.colors.onSurface,
            contentDescription = null
        )

        if (pokemonInfo is Resource.Success) {
            pokemonInfo.data?.sprites?.let {
                Log.e("TAG", "DetailScreen: ${it.front_default}")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it.front_default)
                        .size(600)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter),
//                        .offset(y = 30.dp),
                    contentDescription = pokemonInfo.data.name,

                    )
            }
        }
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
            PokemonDetail(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
//                    .offset(y = (-10).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetail(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name}",
            fontSize = 30.sp,
            letterSpacing = 1.sp,
            fontFamily = poky,
        )
        PokemonType(types = pokemonInfo.types)

        // weight and height
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        Spacer(modifier = Modifier.height(16.dp))

        PokemonBaseStats(pokemonInfo = pokemonInfo)

    }

}


@Composable
fun PokemonType(types: List<Type>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
    ) {
        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    fontFamily = poky,
                    letterSpacing = 1.sp,
                    fontSize = 24.sp
                )
            }
        }
    }

}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember { pokemonWeight * 100f / 1000f }
    val pokemonHeightInMeters = remember { pokemonHeight * 100f / 1000f }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue $dataUnit",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        for (i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
//    PokemonDetail(pokemonInfo = Resource.Loading())
}

