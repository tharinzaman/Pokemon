package com.example.pokemon

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.pokemon.StatsFiles.PokemonModel

object Constants {

    // API URL
    const val BASE_URL: String = "https://pokeapi.co/api/v2/"

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
                // If they are using wifi, cellular or ethernet, return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // Otherwise if they are not connected, return false
                else -> false
            }
        }
        // Else if the build version is older
        else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}