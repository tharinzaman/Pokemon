package com.example.pokemon

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pokemon.InitialFile.InitialFileList
import com.example.pokemon.InitialFile.InitialFileService
import com.example.pokemon.StatsFiles.StatsFilesService
import com.example.pokemon.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // For view binding UI components
    private var binding: ActivityMainBinding? = null
    private lateinit var hyperlinksList: ArrayList<String>
    private lateinit var positionInHyperlinksList: Int

    /**
     * Shared Preferences will be set up so that pokemon stats that have already been previously loaded
     * remain saved and will still appear even without an internet connection.
     */
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For view binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hyperlinksList = ArrayList()
        getPokemonJsonLinks()
        positionInHyperlinksList = 1
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
            val listCall: Call<InitialFileList> = service.getList()

            // Start the parsing:
            listCall.enqueue(object : Callback<InitialFileList> {
                override fun onResponse(
                    call: Call<InitialFileList>,
                    response: Response<InitialFileList>
                ) {
                    // If successful, create a list of type FirstFileList from the body:
                    if (response.isSuccessful) {
                        val firstFileList: InitialFileList? = response.body()
                        // Create a list of hyperlinks from the firstFileList:
                        if (firstFileList != null) {
                            for (i in firstFileList.results){
                                hyperlinksList.add(i.url)
                            }
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
                override fun onFailure(call: Call<InitialFileList>, t: Throwable) {
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

    private fun getPokemonStats() {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {

            // Loop through all the URLs in the hyperlinkList
            for (link in hyperlinksList){
                // Set up retrofit:
                val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service: StatsFilesService = retroFit.create(StatsFilesService::class.java)
                val listCall: Call<InitialFileList> = service.getList(positionInHyperlinksList)

                // Start the parsing:
                listCall.enqueue(object : Callback<InitialFileList> {
                    override fun onResponse(
                        call: Call<InitialFileList>,
                        response: Response<InitialFileList>
                    ) {
                        // If successful, create a list of type FirstFileList from the body:
                        if (response.isSuccessful) {
                            val firstFileList: InitialFileList? = response.body()
                            // Create a list of hyperlinks from the firstFileList:
                            if (firstFileList != null) {
                                for (i in firstFileList.results){
                                    hyperlinksList.add(i.url)
                                }
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
                    override fun onFailure(call: Call<InitialFileList>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })
            }
        }
    }
}