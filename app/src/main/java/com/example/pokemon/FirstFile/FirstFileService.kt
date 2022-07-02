package com.example.pokemon.FirstFile

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface FirstFileService {

    @GET("pokemon/")
    fun getList() : Call<FirstFileList>
}