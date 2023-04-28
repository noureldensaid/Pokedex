package com.fyp.pokedex.models.pokemon

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)