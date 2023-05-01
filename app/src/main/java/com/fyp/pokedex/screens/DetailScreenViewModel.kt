package com.fyp.pokedex.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fyp.pokedex.data.repositorey.Repository
import com.fyp.pokedex.models.pokemon.Pokemon
import com.fyp.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }

//    var pokemonInfo by mutableStateOf<Resource<Pokemon>>(Resource.Loading())
//    var loadError by mutableStateOf("")
//    var isLoading by mutableStateOf(false)
//
//    fun getPokemonInfo(name: String) {
//        viewModelScope.launch {
//            when (val result = repository.getPokemonInfo(name)) {
//                is Resource.Success -> {
//                    pokemonInfo = result
//                    loadError = ""
//                    isLoading = false
//                }
//                is Resource.Error -> {
//                    loadError = result.message!!
//                    isLoading = false
//                }
//                is Resource.Loading -> {
//                    isLoading = true
//                }
//            }
//        }
//    }


}