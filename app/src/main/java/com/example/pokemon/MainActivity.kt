package com.example.pokemon

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.pokemon.InitialFile.InitialFileModel
import com.example.pokemon.InitialFile.InitialFileService
import com.example.pokemon.StatsFiles.PokemonModel
import com.example.pokemon.StatsFiles.StatsFileService
import com.example.pokemon.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso
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
                        val hyperlinksList = ArrayList<String>()
                        if (firstFileList != null) {
                            for (i in firstFileList.results) {
                                hyperlinksList.add(i.url)
                                Log.i("link", "$i")
                            }
                            Log.i("Hyperlink list inner", "$hyperlinksList")
                            getPokemonStats(hyperlinksList)
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
            var servicePosition = 0
            var listPosition = 1

            // Loop through all the URLs in the hyperlinkList
            for (link in list) {
                // Set up retrofit:
                val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service: StatsFileService = retroFit.create(StatsFileService::class.java)
                servicePosition += 1
                Log.i("servicePosition", "$servicePosition")
                val listCall: Call<PokemonModel> = service.getList(servicePosition)

                // Start the parsing:
                listCall.enqueue(object : Callback<PokemonModel> {
                    override fun onResponse(
                        call: Call<PokemonModel>,
                        response: Response<PokemonModel>
                    ) {
                        // If successful, create a pokemon object, convert it to...
                        if (response.isSuccessful) {
                            Log.i("link", "$link")
                            var pokemon: PokemonModel? = response.body()
                            Log.i("Pokemon", "$pokemon")
                            if (pokemon != null) {
                                Log.i("PositionInner", "$listPosition")
                                setupCard(listPosition, pokemon)
                                listPosition++
                            }
                            // If placed here, the position increases and UI gets set up but only Bulbasaur gets looped through.
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
                        // If placed here, it also only generates Bulbasaur
                    }
                    // If failed, show error message in Logcat:
                    override fun onFailure(call: Call<PokemonModel>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })

                // If placed here, the position increases and the links are all looped through, but UI cannot be set up.
            }
        }
        hideProgressDialog()
    }


    /**
     * This method will set up the material card views in the Main Activities UI which consists of a
     * scroll view with 20 material cards views, each containing the sprite and name of distinct pokemons.
     */
    private fun setupCard(position: Int, pokemon: PokemonModel) {

//             When statement for the different pokemons
        when (position) {
            1 -> {
                binding?.tv1?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv1)
                Log.i("Success", "Success for UI")
            }
            2 -> {
                binding?.tv2?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv2)
                Log.i("Success", "Success for UI")
            }
            3 -> {
                binding?.tv3?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv3)
                Log.i("Success", "Success for UI")
            }
            4 -> {
                binding?.tv4?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv4)
                Log.i("Success", "Success for UI")
            }
            5 -> {
                binding?.tv5?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv5)
                Log.i("Success", "Success for UI")
            }
            6 -> {
                binding?.tv6?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv6)
                Log.i("Success", "Success for UI")
            }
            7 -> {
                binding?.tv7?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv7)
                Log.i("Success", "Success for UI")
            }
            8 -> {
                binding?.tv8?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv8)
                Log.i("Success", "Success for UI")
            }
            9 -> {
                binding?.tv9?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv9)
                Log.i("Success", "Success for UI")
            }
            10 -> {
                binding?.tv10?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv10)
                Log.i("Success", "Success for UI")
            }
            11 -> {
                binding?.tv11?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv11)
                Log.i("Success", "Success for UI")
            }
            12 -> {
                binding?.tv12?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv12)
                Log.i("Success", "Success for UI")
            }
            13 -> {
                binding?.tv2?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv13)
                Log.i("Success", "Success for UI")
            }
            14 -> {
                binding?.tv14?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv14)
                Log.i("Success", "Success for UI")
            }
            15 -> {
                binding?.tv15?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv15)
                Log.i("Success", "Success for UI")
            }
            16 -> {
                binding?.tv16?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv16)
                Log.i("Success", "Success for UI")
            }
            17 -> {
                binding?.tv17?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv17)
                Log.i("Success", "Success for UI")
            }
            18 -> {
                binding?.tv18?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv18)
                Log.i("Success", "Success for UI")
            }
            19 -> {
                binding?.tv19?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv19)
                Log.i("Success", "Success for UI")
            }
            20 -> {
                binding?.tv20?.text = pokemon.name.capitalize()
                Picasso
                    .with(this)
                    .load(pokemon.sprites.image)
                    .into(binding?.iv20)
                Log.i("Success", "Success for UI")
            }
            else -> {
                Log.i("Failed", "Could not set up UI")
            }
        }
    }


    private fun setTextImageAndOnClick(
        textview: TextView, imageView: ImageView, pokemon: PokemonModel
    ) {
        textview.text = pokemon.name.capitalize()
        Picasso
            .with(this)
            .load(pokemon.sprites.image)
            .into(imageView)
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

