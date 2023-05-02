package com.fyp.pokedex.screens

import androidx.lifecycle.ViewModel
import com.fyp.pokedex.data.repositorey.Repository
import com.fyp.pokedex.models.pokemon.Pokemon
import com.fyp.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}