package com.example.pokemon.FirstFile

import java.io.Serializable

data class FirstFileList(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<FirstFileResult>
) : Serializable
