package com.example.pokemon.StatsFiles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PokemonModel(
    val name: String,
    val height: Int,
    val weight: Int,
    val id: Int,
    val sprites: Sprites,
    val stats: List<Stat>
) : Serializable

data class Sprites(
    @SerializedName("front_default")
    val image: String
) : Serializable

data class Stat(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: StatName
) : Serializable

data class StatName(
    val name: String
) : Serializable



