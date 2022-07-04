package com.example.pokemon

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pokemon.InitialFile.InitialFileModel
import com.example.pokemon.InitialFile.InitialFileService
import com.example.pokemon.StatsFiles.PokemonModel
import com.example.pokemon.StatsFiles.StatsFileService
import com.example.pokemon.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // For view binding UI components
    private var binding: ActivityMainBinding? = null

    // For the shared preferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // Progress Dialog
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For view binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // Shared Preferences
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        // Start the method
        getPokemonJsonLinks()
    }

    /**
     * This method will first check if the user is connected to the internet, then load in the
     * data from the first JSON file containing the Pokemon names and hyperlinks to the JSON files
     * containing their stats. It will create a list of these hyperlinks and then call the getPokemonStats
     * method with the hyperlinks list as the parameter.
     */
    private fun getPokemonJsonLinks() {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {

            // Set up retrofit:
            val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: InitialFileService = retroFit.create(InitialFileService::class.java)
            val listCall: Call<InitialFileModel> = service.getList()

            // Start the parsing:
            showProgressDialog()
            listCall.enqueue(object : Callback<InitialFileModel> {
                override fun onResponse(
                    call: Call<InitialFileModel>,
                    response: Response<InitialFileModel>
                ) {
                    // If successful, create a list of type FirstFileList from the body:
                    if (response.isSuccessful) {
                        val firstFileList: InitialFileModel? = response.body()
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
                override fun onFailure(call: Call<InitialFileModel>, t: Throwable) {
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

    /**
     * This method will first check if the user is connected to the internet, and then loop through
     * all of the hyperlinks in the list parameter. It will create pokemon objects, convert these to
     * strings and then place these strings into the app's shared preferences. It will then call the
     * setupCard method so that each card in the UI of the Main Activity is set up.
     */
    private fun getPokemonStats(list: ArrayList<String>) {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {
            // Position in hyperlink list
            var position = 1

            // Loop through all the URLs in the hyperlinkList
            for (link in list) {
                // Set up retrofit:
                val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service: StatsFileService = retroFit.create(StatsFileService::class.java)
                val listCall: Call<PokemonModel> = service.getList(position)

                // Start the parsing:
                listCall.enqueue(object : Callback<PokemonModel> {
                    override fun onResponse(
                        call: Call<PokemonModel>,
                        response: Response<PokemonModel>
                    ) {
                        // If successful, create a pokemon object, convert it to...
                        if (response.isSuccessful) {
                            val pokemon: PokemonModel? = response.body()
                            val pokemonJsonString = Gson().toJson(pokemon)
                            // Put it into the const val that matches the current position
                            editor.putString("${Constants}.$position", pokemonJsonString)
                            editor.apply()
                            setupCard(position)
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
                    override fun onFailure(call: Call<PokemonModel>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })
                // Increase position
                position++
            }
            hideProgressDialog()
        }
    }

    /**
     * This method will set up the material card views in the Main Activities UI which consists of a
     * scroll view with 20 material cards views, each containing the sprite and name of distinct pokemons.
     */
    private fun setupCard(cardNum: Int) {
        // Get the string
        val pokemonJsonString = sharedPreferences.getString("${Constants}.$cardNum", "")

        // If the Json string isn't empty, then convert back to Gson and set up the UI elements
        if (!pokemonJsonString.isNullOrEmpty()) {
            val pokemonList =
                Gson().fromJson(pokemonJsonString, PokemonModel::class.java)

        }
    }

    /**
     * This method will display a circular progress bar. It will be called and displayed whilst the
     * app is retrieving data from the API.
     */
    private fun showProgressDialog() {
        progressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        progressDialog!!.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        progressDialog!!.show()
    }

    /**
     * This method will hide the progress bar. It will be called once all of the data has been
     * retrieved from the API and the UI elements have been set up.
     */
    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

}