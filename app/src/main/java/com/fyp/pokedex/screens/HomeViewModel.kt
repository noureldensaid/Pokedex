package com.fyp.pokedex.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {


    private var curPage = 0
    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        getPokemonList()
    }

    fun getPokemonList() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(20, curPage * 20)
            when (result) {
                is Resource.Success -> {
                    endReached.value = curPage * 20 >= result.data!!.count
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
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                    Log.e("DATA", "getPokemonList: ${pokemonList.value.size}")
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
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


}