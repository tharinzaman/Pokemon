package com.example.pokemon

import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pokemon.FirstFile.FirstFileList
import com.example.pokemon.FirstFile.FirstFileService
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
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For view binding UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    /**
     * This method will first check if the user is connected to the internet, then load in the
     * data from the first JSON file containing the Pokemon names and hyperlinks to the JSON files containing
     * their stats.
     */
    private fun getPokemonJsonLinks() {
        // Check if they are connected to the internet:
        if (Constants.checkIfNetworkIsAvailable(this)) {

            // Set up retrofit:
            val retroFit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: FirstFileService = retroFit.create(FirstFileService::class.java)
            val listCall: Call<FirstFileList> = service.getList()

            // Start the parsing:
            listCall.enqueue(object : Callback<FirstFileList> {
                override fun onResponse(
                    call: Call<FirstFileList>,
                    response: Response<FirstFileList>
                ) {
                    // If successful, create a list of type FirstFileList from the body:
                    if (response.isSuccessful) {
                        val firstFileList: FirstFileList? = response.body()

                        Log.i("Response Result", "$firstFileList")
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
                override fun onFailure(call: Call<FirstFileList>, t: Throwable) {
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
}