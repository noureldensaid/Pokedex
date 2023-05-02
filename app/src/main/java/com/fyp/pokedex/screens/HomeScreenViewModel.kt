package com.fyp.pokedex.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.fyp.pokedex.data.repositorey.Repository
import com.fyp.pokedex.models.pokemonList.PokedexListEntry
import com.fyp.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {



    private var curPage = 0
    var pokemonList by mutableStateOf<List<PokedexListEntry>>(listOf())
    var pokemonSearchResults = mutableStateOf<PokedexListEntry>(PokedexListEntry("", "", 0))
    var loadError by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var endReached by mutableStateOf(false)

    init {
        getPokemonList()
    }

    fun getPokemonList() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getPokemonList(20, curPage * 20)
            when (result) {
                is Resource.Success -> {
                    endReached = curPage * 20 >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    curPage++
                    loadError = ""
                    isLoading = false
                    pokemonList += pokedexEntries
                    Log.e("DATA", "getPokemonList: ${pokemonList.size}")
                }
                is Resource.Error -> {
                    loadError = result.message!!
                    isLoading = false
                }
                else -> {}
            }
        }
    }

    // calculate the dominate color
    fun calculateDominateColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        // cast drawable into bitmap so pallet can use it
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun getPokemonInfo(name: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getPokemonInfo(name)
            when (result) {
                is Resource.Success -> {
                    val pokemonInfo = result.data
                    if (pokemonInfo != null) {
                        pokemonSearchResults.value = PokedexListEntry(
                            pokemonInfo.name,
                            pokemonInfo.sprites.front_shiny,
                            pokemonInfo.order
                        )
                    }
                    loadError = ""
                    isLoading = false
                    Log.e("DATA", "getPokemonList: ${pokemonList.size}")
                }
                is Resource.Error -> {
                    loadError = result.message!!
                    isLoading = false
                }
                else -> {}
            }
        }
    }

}