package com.fyp.pokedex.data.repositorey

import android.util.Log
import com.fyp.pokedex.data.remote.PokeApi
import com.fyp.pokedex.models.pokemon.Pokemon
import com.fyp.pokedex.models.pokemonList.PokemonList
import com.fyp.pokedex.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val api: PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            Log.e("Error", "getPokemonList: $e")
            return Resource.Error("Can't fetch pokemon list" + e.message)
        }
        return Resource.Success(response)
    }


    suspend fun getPokemonInfo(name: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(name)
        } catch (e: Exception) {
            Log.e("Error", "getPokemonInfo: $e")
            return Resource.Error("Can't fetch pokemon list")
        }
        return Resource.Success(response)
    }


}