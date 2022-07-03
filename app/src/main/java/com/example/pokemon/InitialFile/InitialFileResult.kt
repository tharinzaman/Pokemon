package com.example.pokemon.InitialFile

import java.io.Serializable

data class InitialFileResult(
    val name: String,
    val url: String
) : Serializable
