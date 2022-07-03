package com.example.pokemon

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pokemon.Constants.pokemonList
import com.example.pokemon.InitialFile.InitialFileObject
import com.example.pokemon.InitialFile.InitialFileService
import com.example.pokemon.StatsFiles.PokemonObject
import com.example.pokemon.StatsFiles.StatsFileService
import com.example.pokemon.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // For view binding UI components
    private var binding: ActivityMainBinding? = null

    /**
     * Shared Preferences will be set up so that pokemon stats that have already been previously loaded
     * remain saved and will still appear even without an internet connection.
     */
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For view binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        pokemonList
        getPokemonJsonLinks()
    }

    /**
     * This method will first check if the user is connected to the internet, then load in the
     * data from the first JSON file containing the Pokemon names and hyperlinks to the JSON files containing
     * their stats. It will then add these hyperlinks to the 'hyperlinksList'.
     */
    private fun getPokemonJsonLinks() {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {

            // Set up retrofit:
            val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: InitialFileService = retroFit.create(InitialFileService::class.java)
            val listCall: Call<InitialFileObject> = service.getList()

            // Start the parsing:
            listCall.enqueue(object : Callback<InitialFileObject> {
                override fun onResponse(
                    call: Call<InitialFileObject>,
                    response: Response<InitialFileObject>
                ) {
                    // If successful, create a list of type FirstFileList from the body:
                    if (response.isSuccessful) {
                        val firstFileList: InitialFileObject? = response.body()
                        // Add to the list of hyperlinks from the firstFileList:
                        val hyperlinksListInner = ArrayList<String>()
                        if (firstFileList != null) {
                            for (i in firstFileList.results) {
                                hyperlinksListInner.add(i.url)
                                Log.i("link", "$i")
                            }
                            Log.i("Hyperlink list inner", "$hyperlinksListInner")
                            getPokemonStats(hyperlinksListInner)
                        }
                    } // Else if not successful, show the codes for why it failed:
                    else {
                        val rc = response.code()
                        when (rc) {
                            400 -> {
                                Log.e("Error 400", "Bad connection")
                            }
                            404 -> {
                                Log.e("Error 404", "Not found")
                            }
                            else -> {
                                Log.e("Error", "Generic error")
                            }
                        }
                    }
                }

                // If failed, show error message in Logcat:
                override fun onFailure(call: Call<InitialFileObject>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
        } // Else if not connected to the internet, show a toast message:
        else {
            Toast.makeText(
                this@MainActivity,
                "You have no connection to the internet", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getPokemonStats(list: ArrayList<String>) {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {
            // Position in hyperlink list
            var position = 1

            // Loop through all the URLs in the hyperlinkList
            for (link in list){
                // Set up retrofit:
                val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service: StatsFileService = retroFit.create(StatsFileService::class.java)
                val listCall: Call<PokemonObject> = service.getList(position)

                // Start the parsing:
                listCall.enqueue(object : Callback<PokemonObject> {
                    override fun onResponse(
                        call: Call<PokemonObject>,
                        response: Response<PokemonObject>
                    ) {
                        // If successful, create a pokemon object and add it to the pokemon list
                        if (response.isSuccessful) {
                            val pokemon: PokemonObject? = response.body()
                            pokemonList.add(pokemon!!)
                            Log.i("Pokemon", "$pokemon")
                        } // Else if not successful, show the codes for why it failed:
                        else {
                            val rc = response.code()
                            when (rc) {
                                400 -> {
                                    Log.e("Error 400", "Bad connection")
                                }
                                404 -> {
                                    Log.e("Error 404", "Not found")
                                }
                                else -> {
                                    Log.e("Error", "Generic error")
                                }
                            }
                        }
                    }

                    // If failed, show error message in Logcat:
                    override fun onFailure(call: Call<PokemonObject>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })
                // Increase position
                position++
            }
            Log.i("List 2", "$pokemonList")
        }
    }
}