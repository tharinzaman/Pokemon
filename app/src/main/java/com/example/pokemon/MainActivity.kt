package com.example.pokemon

/**
 * This is my first app using a REST API and Retrofit. It reads in data from an endpoint from the
 * Pokemon API (https://pokeapi.co/api/v2/pokemon/). This contains a series of 20 other endpoints
 * containing data and stats about pokemons, which are all read in using Retrofit. The data from this
 * is then used to build a UI which comprises a list of pokemons with their names and sprites. Upon
 * clicking the pokemon, the user will be directed to the DetailsScreen Activity which will show them
 * more stats and information about the pokemon, again derived from the aPI. The GitHub link for this
 * app is https://github.com/tharinzaman/Pokemon.
 */

import android.app.Dialog
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

    // View binding for UI components
    private var binding: ActivityMainBinding? = null

    // Progress Dialog
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // Get the API data
        getPokemonJsonLinks()
    }

    /**
     * This method will first check if the user is connected to the internet. If they are, then it will load in the
     * data from the initial file (https://pokeapi.co/api/v2/pokemon/) containing the Pokemon names and links to the files
     * containing greater details and stats. It will create a list of these links and then call the getPokemonStats()
     * method with this list as the parameter.
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

            // Begin attempt to retrieve the data
            showProgressDialog() // Show the progress dialog whilst the data is being retrieved and UI is being set up
            listCall.enqueue(object : Callback<InitialFileModel> {
                override fun onResponse(
                    call: Call<InitialFileModel>,
                    response: Response<InitialFileModel>
                ) {
                    // If successful, create a list of links and then call the getPokemonStats() method
                    if (response.isSuccessful) {
                        val initialFileList: InitialFileModel? = response.body()
                        val linksList = ArrayList<String>()
                        if (initialFileList != null) {
                            for (i in initialFileList.results) {
                                linksList.add(i.url)
                            }
                            getPokemonStats(linksList)
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
     * This method will first check if the user is connected to the internet. If they are, then it will loop through
     * all of the links in the list parameter. When doing this, it will create pokemon objects, and call the setupCard()
     * method to set up the UI of the Main Activity.
     */
    private fun getPokemonStats(list: ArrayList<String>) {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {

            // Positions in list, these have to be separate or the method won't work.
            var servicePosition = 0
            var listPosition = 1

            // Loop through all the URLs in the list
            for (link in list) {
                // Set up retrofit:
                val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service: StatsFileService = retroFit.create(StatsFileService::class.java)
                // Must increment here or only one pokemon object will be created each time
                servicePosition += 1
                val listCall: Call<PokemonModel> = service.getList(servicePosition)

                // Begin attempt to retrieve data:
                listCall.enqueue(object : Callback<PokemonModel> {
                    override fun onResponse(
                        call: Call<PokemonModel>,
                        response: Response<PokemonModel>
                    ) {
                        // If successful, create a pokemon object, and call the setupCard method with it as a parameter
                        if (response.isSuccessful) {
                            var pokemon: PokemonModel? = response.body()
                            if (pokemon != null) {
                                setupCard(listPosition, pokemon)
                                listPosition++ // Must increment here in order for code to work
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
                    override fun onFailure(call: Call<PokemonModel>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })

            }
        }
        // Once all of the cards have been set up, hide the progress dialog.
        hideProgressDialog()
    }

    /**
     * This method will set up the material card views in the Main Activity's UI by calling the
     * setTextImageAndOnClickMethod and passing the appropriate parameters.
     */
    private fun setupCard(position: Int, pokemon: PokemonModel) {
        // Setting up the cardview and its components depending on the position in the hyperlinks list
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

    /**
     * This method sets up the card view by using the parameters parsed to it. It sets the OnClickListeners
     * of the cards to start a new DetailsScreen activity when they are clicked. It passes the pokemon
     * object as an intent when starting this activity.
     */
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
        // Set the onClickListener
        materialCardView?.setOnClickListener{
            val intent = Intent(this, DetailsScreen::class.java)
            intent.putExtra("pokemon", pokemon)
            startActivity(intent)
        }
    }

    /**
     * This method will display a circular progress bar. It will be called and displayed whilst the
     * app is retrieving data from the API.
     */
    private fun showProgressDialog() {
        progressDialog = Dialog(this)

        /*Set the screen content from the layout resource.
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

    // Destroy binding to avoid data leaks
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

