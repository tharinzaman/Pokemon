package com.example.pokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DetailsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_screen)
    }

    // Finish the activity when back is pressed in order to avoid
    override fun onBackPressed() {
        this.finish()
    }
}