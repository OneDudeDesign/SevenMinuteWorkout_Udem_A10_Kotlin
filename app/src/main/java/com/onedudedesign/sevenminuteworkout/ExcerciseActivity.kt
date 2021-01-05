package com.onedudedesign.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_excercise.*

class ExcerciseActivity : AppCompatActivity() {

    //set variables for the rest timer
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        setSupportActionBar(toolbar_excercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_excercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }
        setupRestView()
    }

    //when the activity is destroyed cancel the timer and reset the restProgress
    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }
        super.onDestroy()
    }

    //setup the rest progressbar and start it
    private fun setRestProgressBar(){
        //tell the progressbar what its progress is to force the segment removal in the drawables
        progressbar.progress = restProgress
        restTimer = object: CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressbar.progress = 10 - restProgress
                tvtimer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                Toast.makeText(this@ExcerciseActivity,
                    "Here now we will start the excercise",Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    //called from oncreate this sets up the view resetting the timer if it is not null and
    // resetting the restprogress variable so that it starts the timer at 10 again
    private fun setupRestView(){
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        setRestProgressBar()
    }
}