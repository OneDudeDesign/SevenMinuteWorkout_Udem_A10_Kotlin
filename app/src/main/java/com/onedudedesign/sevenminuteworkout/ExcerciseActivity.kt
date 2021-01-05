package com.onedudedesign.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_excercise.*

class ExcerciseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        setSupportActionBar(toolbar_excercise_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_excercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}