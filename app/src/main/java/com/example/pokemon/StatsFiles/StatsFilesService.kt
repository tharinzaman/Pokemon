package com.example.pokemon.StatsFiles

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsFilesService {

    @GET("pokemon/")
    fun getList(
        @Query("") num: Int
    ) : Call<PokemonObject>
}