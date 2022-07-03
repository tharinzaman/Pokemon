package com.example.pokemon.StatsFiles

import java.io.Serializable

data class StatsFileList(
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: List<String>,
    val stats: List<Stat>


) : Serializable

data class Stat (
    val baseStat: Int,
    val stat: StatName
) : Serializable

data class StatName (
    val name: String
) : Serializable

