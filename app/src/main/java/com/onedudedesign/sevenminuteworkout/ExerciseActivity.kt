package com.onedudedesign.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_excercise.*

class ExerciseActivity : AppCompatActivity() {

    //timer tick val
    private val tickValue = 1000L

    //set variables for the rest timer
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private val restDuration = 3000L

    //set the variables for the excercise timer
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private val exerciseDuration = 3000L

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        setSupportActionBar(toolbar_excercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_excercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        exerciseList = Constants.defaultExerciseList()

        //start the restview and counter
        setupRestView()
    }

    //when the activity is destroyed cancel the timers and reset the progress
    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        super.onDestroy()
    }

    //setup the rest progressbar and start it
    private fun setRestProgressBar(){
        //tell the progressbar what its progress is to force the segment removal in the drawables
        progressbar.progress = restProgress
        restTimer = object: CountDownTimer(restDuration,tickValue){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressbar.progress = (restDuration/1000).toInt() - restProgress
                tvtimer.text = ((restDuration/1000).toInt() - restProgress).toString()
            }

            override fun onFinish() {
                //currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }

    //setup the exercise progress bar
    private fun setExerciseProgressBar(){
        //tell the progressbar what its progress is to force the segment removal in the drawables
        exerciseprogressbar.progress = exerciseProgress
        exerciseTimer = object: CountDownTimer(exerciseDuration,tickValue){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                exerciseprogressbar.progress = (exerciseDuration/1000).toInt() - exerciseProgress
                exercisetvtimer.text = ((exerciseDuration/1000).toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList!!.size -1){
                    setupRestView()
                } else {
                    Toast.makeText(this@ExerciseActivity,
                        "congratulations you have completed the Seven Minute Workout!!",
                        Toast.LENGTH_SHORT).show()

                }

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

        llexerciseview.visibility=View.GONE
        llRestView.visibility=View.VISIBLE
        currentExercisePosition++
        tvRestExerciseName.text = exerciseList!![currentExercisePosition].getName()

        setRestProgressBar()
    }

    //called when rest finishes, switches the layout and starts the exercise
    private fun setupExerciseView(){
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        llRestView.visibility = View.GONE
        llexerciseview.visibility = View.VISIBLE

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()


    }
}