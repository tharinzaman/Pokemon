package com.example.pokemon.StatsFiles

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsFileService {

    @GET("pokemon/{ID}")
    fun getList(
        @Path("ID") pokemonID: Int
    ) : Call<PokemonModel>
}