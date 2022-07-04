package com.example.pokemon

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // For view binding UI components
    private var binding: ActivityMainBinding? = null

    // Progress Dialog
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For view binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
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

            // Positions in hyperlink list, these have to be separate or the method won't work.
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

        when (position) {
            1 -> { setTextImageAndOnClick(binding?.tv1, binding?.iv1, binding?.card1, pokemon) }
            2 -> { setTextImageAndOnClick(binding?.tv2, binding?.iv2, binding?.card2, pokemon) }
            3 -> { setTextImageAndOnClick(binding?.tv3, binding?.iv3, binding?.card3, pokemon) }
            4 -> { setTextImageAndOnClick(binding?.tv4, binding?.iv4, binding?.card4, pokemon) }
            5 -> { setTextImageAndOnClick(binding?.tv5, binding?.iv5, binding?.card5, pokemon) }
            6 -> { setTextImageAndOnClick(binding?.tv6, binding?.iv6, binding?.card6, pokemon) }
            7 -> { setTextImageAndOnClick(binding?.tv7, binding?.iv7, binding?.card7, pokemon) }
            8 -> { setTextImageAndOnClick(binding?.tv8, binding?.iv8, binding?.card8, pokemon) }
            9 -> { setTextImageAndOnClick(binding?.tv9, binding?.iv9, binding?.card9, pokemon) }
            10 -> { setTextImageAndOnClick(binding?.tv10, binding?.iv10, binding?.card10, pokemon) }
            11 -> { setTextImageAndOnClick(binding?.tv11, binding?.iv11, binding?.card11, pokemon) }
            12 -> { setTextImageAndOnClick(binding?.tv12, binding?.iv12, binding?.card12, pokemon) }
            13 -> { setTextImageAndOnClick(binding?.tv13, binding?.iv13, binding?.card13, pokemon) }
            14 -> { setTextImageAndOnClick(binding?.tv14, binding?.iv14, binding?.card14, pokemon) }
            15 -> { setTextImageAndOnClick(binding?.tv15, binding?.iv15, binding?.card15, pokemon) }
            16 -> { setTextImageAndOnClick(binding?.tv16, binding?.iv16, binding?.card16, pokemon) }
            17 -> { setTextImageAndOnClick(binding?.tv17, binding?.iv17, binding?.card17, pokemon) }
            18 -> { setTextImageAndOnClick(binding?.tv18, binding?.iv18, binding?.card18, pokemon) }
            19 -> { setTextImageAndOnClick(binding?.tv19, binding?.iv19, binding?.card19, pokemon) }
            20 -> { setTextImageAndOnClick(binding?.tv20, binding?.iv20, binding?.card20, pokemon) }
            else -> {
                Log.i("Failed", "Could not set up UI")
            }
        }
    }

    private fun setTextImageAndOnClick(
        textview: TextView?,
        imageView: ImageView?,
        materialCardView: MaterialCardView?,
        pokemon: PokemonModel) {

        // Set the TextView
        textview?.text = pokemon.name.capitalize()
        // Set the ImageView
        Picasso
            .with(this)
            .load(pokemon.sprites.image)
            .into(imageView)
        // Set the onClickListener for the card
        materialCardView?.setOnClickListener{
            startActivity(Intent(this, DetailsScreen::class.java))
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

