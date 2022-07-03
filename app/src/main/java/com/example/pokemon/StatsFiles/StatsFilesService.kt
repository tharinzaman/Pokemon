package com.example.pokemon.StatsFiles

import com.example.pokemon.InitialFile.InitialFileList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsFilesService {

    @GET("pokemon/")
    fun getList(
        @Query("") num: Int
    ) : Call<InitialFileList>
}