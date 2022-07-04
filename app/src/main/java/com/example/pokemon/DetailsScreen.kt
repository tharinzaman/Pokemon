package com.example.pokemon

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pokemon.StatsFiles.PokemonModel
import com.example.pokemon.databinding.ActivityDetailsScreenBinding
import com.squareup.picasso.Picasso

class DetailsScreen : AppCompatActivity() {

    // Set up view binding
    var binding: ActivityDetailsScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View binding set up
        binding = ActivityDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // Get the pokemon object
        var pokemon = intent.getSerializableExtra("pokemon") as PokemonModel
        // Set the UI variables:
        Picasso
            .with(this@DetailsScreen)
            .load(pokemon.sprites.image)
            .into(binding?.ivDetailScreenImage)
        binding?.tvName?.text = "Name: ${pokemon.name.capitalize()}"
        binding?.tvHeight?.text = "Height: ${pokemon.height}"
        binding?.tvWeight?.text = "Weight: ${pokemon.weight}"
        binding?.tvStat1?.text = "${pokemon.stats[1].stat.name.capitalize()}: ${pokemon.stats[1].baseStat}"
        binding?.tvStat2?.text = "${pokemon.stats[2].stat.name.capitalize()}: ${pokemon.stats[2].baseStat}"
        binding?.tvStat3?.text = "${pokemon.stats[3].stat.name.capitalize()}: ${pokemon.stats[3].baseStat}"
        binding?.tvStat4?.text = "${pokemon.stats[4].stat.name.capitalize()}: ${pokemon.stats[4].baseStat}"
        binding?.tvStat5?.text = "${pokemon.stats[5].stat.name.capitalize()}: ${pokemon.stats[5].baseStat}"
    }

    // Finish the activity when back is pressed in order to avoid multiple activities being open at once
    override fun onBackPressed() {
        this.finish()
    }

    // Destroy the view binding to avoid data leaks
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}