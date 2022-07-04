package com.example.pokemon

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.pokemon.StatsFiles.PokemonModel

object Constants {

    // API URL
    const val BASE_URL: String = "https://pokeapi.co/api/v2/"

    // For shared preferences:
    const val PREFERENCE_NAME = "PokemonAppPreferences"
    const val POKEMON_1 = "pokemon_1"
    const val POKEMON_2 = "pokemon_2"
    const val POKEMON_3 = "pokemon_3"
    const val POKEMON_4 = "pokemon_4"
    const val POKEMON_5 = "pokemon_5"
    const val POKEMON_6 = "pokemon_6"
    const val POKEMON_7 = "pokemon_7"
    const val POKEMON_8 = "pokemon_8"
    const val POKEMON_9 = "pokemon_9"
    const val POKEMON_10 = "pokemon_10"
    const val POKEMON_11 = "pokemon_11"
    const val POKEMON_12 = "pokemon_12"
    const val POKEMON_13 = "pokemon_13"
    const val POKEMON_14 = "pokemon_14"
    const val POKEMON_15 = "pokemon_15"
    const val POKEMON_16 = "pokemon_16"
    const val POKEMON_17 = "pokemon_17"
    const val POKEMON_18 = "pokemon_18"
    const val POKEMON_19 = "pokemon_19"
    const val POKEMON_20 = "pokemon_20"

    /**
     * This method checks to see if the user is connected to the internet, and then returns a boolean,
     * 'true' if they are, and 'false' if they're not.
     */
    fun checkIfNetworkIsAvailable(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager

        // If the build version is newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // If they are using wifi, celluar or ethernet, return true, as they are connected to the internet:
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // Otherwise if they are not connected, return false:
                else -> false
            }
        }
        // Else if the build version is older:
        else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}