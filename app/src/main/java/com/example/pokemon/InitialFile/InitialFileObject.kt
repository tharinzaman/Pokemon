package com.example.pokemon.InitialFile

import java.io.Serializable

data class InitialFileObject(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<InitialFileResult>
) : Serializable

data class InitialFileResult(
    val name: String,
    val url: String
) : Serializable

