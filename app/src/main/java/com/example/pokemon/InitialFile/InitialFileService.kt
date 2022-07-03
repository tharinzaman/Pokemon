package com.example.pokemon.InitialFile

import retrofit2.Call
import retrofit2.http.GET

interface InitialFileService {

    @GET("pokemon/")
    fun getList() : Call<InitialFileList>
}