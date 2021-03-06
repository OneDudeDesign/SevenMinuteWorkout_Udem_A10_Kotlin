package com.onedudedesign.sevenminuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_excercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

//the TTS onitListener allows TTS
class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

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

    //initialize a tts object as null for later use
    private var tts: TextToSpeech? = null

    //initialize a mediaplayer object to play sound on finished
    private var player: MediaPlayer? = null

    //set the recyclerview adapter
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        setSupportActionBar(toolbar_excercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_excercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()

        //setup the tts object for this activity listening in this activity
        tts = TextToSpeech(this, this)

        setupExerciseStatusRecyclerView()

        //start the restview and counter
        setupRestView()
    }

    //when the activity is destroyed cancel the timers and reset the progress
    override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        //shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        //kill the player
        if (player != null) {
            player!!.stop()
        }
        super.onDestroy()
    }

    //setup the rest progressbar and start it
    private fun setRestProgressBar() {
        //tell the progressbar what its progress is to force the segment removal in the drawables
        progressbar.progress = restProgress
        restTimer = object : CountDownTimer(restDuration, tickValue) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressbar.progress = (restDuration / 1000).toInt() - restProgress
                tvtimer.text = ((restDuration / 1000).toInt() - restProgress).toString()
            }

            override fun onFinish() {
                //currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    //setup the exercise progress bar
    private fun setExerciseProgressBar() {
        //tell the progressbar what its progress is to force the segment removal in the drawables
        exerciseprogressbar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseDuration, tickValue) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                exerciseprogressbar.progress = (exerciseDuration / 1000).toInt() - exerciseProgress
                exercisetvtimer.text =
                    ((exerciseDuration / 1000).toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList!!.size - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {

                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)

                }

            }
        }.start()
    }

    //called from oncreate this sets up the view resetting the timer if it is not null and
    // resetting the restprogress variable so that it starts the timer at 10 again
    private fun setupRestView() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        try {
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player!!.isLooping = false
            player!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        llexerciseview.visibility = View.GONE
        llRestView.visibility = View.VISIBLE
        currentExercisePosition++
        tvRestExerciseName.text = exerciseList!![currentExercisePosition].getName()

        setRestProgressBar()
    }

    //called when rest finishes, switches the layout and starts the exercise
    private fun setupExerciseView() {
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        llRestView.visibility = View.GONE
        llexerciseview.visibility = View.VISIBLE

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()

        //function call to speak the excercise name
        speakOut(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified  is not supported")
            }
        } else {
            Log.e("TTS", "Initialization of TTS failed")
        }
    }


    //function to do tts
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    //setup the recyclerview
    private fun setupExerciseStatusRecyclerView() {
        rvExerciseStatus.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}