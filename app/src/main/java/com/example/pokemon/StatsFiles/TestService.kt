package com.example.pokemon.StatsFiles

import com.example.pokemon.InitialFile.InitialFileObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TestService {

    @GET("pokemon/{ID}")
    fun getList(
        @Path("ID") pokemonID: Int
    ) : Call<PokemonObject>
}