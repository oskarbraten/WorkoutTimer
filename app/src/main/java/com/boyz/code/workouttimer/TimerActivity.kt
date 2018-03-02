package com.boyz.code.workouttimer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.ExerciseAdapter
import com.boyz.code.workouttimer.misc.WorkoutManager
import com.boyz.code.workouttimer.misc.toTimerFormat
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    // static constants
    companion object {
        const val TIMER_STATE_POSITION = "POSITION"
        const val TIMER_STATE_PROGRESS = "PROGRESS"
        const val TIMER_STATE_PLAYING = "PLAYING"
    }

    private lateinit var workout: Workout
    private lateinit var recyclerView: RecyclerView
    private var sounds: Pair<MediaPlayer, MediaPlayer>? = null

    private var playing: Boolean = false
    private var currentTimer: CountDownTimer? = null
    private var currentProgress: Pair<Int, Long> = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        supportActionBar?.hide()

        val workoutTitle = intent.getStringExtra("title")
        title = workoutTitle

        workout = WorkoutManager.getWorkout(this, workoutTitle)

        recyclerView = findViewById(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = ExerciseAdapter(workout)

        pauseBtn.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    pause()
                }
                else -> {
                    play()
                }
            }
        }

        resetBtn.setOnLongClickListener {
            finish() // close activity
            true
        }

        // initialize media player.
        Thread({
            val shortBeep = MediaPlayer()
            shortBeep.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())

            shortBeep.setDataSource(this, Uri.parse("android.resource://com.boyz.code.workouttimer/" + R.raw.beep))

            shortBeep.setVolume(0.25f, 0.25f)
            shortBeep.prepare()

            val longBeep = MediaPlayer()
            longBeep.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())

            longBeep.setDataSource(this, Uri.parse("android.resource://com.boyz.code.workouttimer/" + R.raw.beep_long))
            longBeep.setVolume(0.35f, 0.35f)
            longBeep.prepare()

            sounds = Pair(shortBeep, longBeep)
        }).run()

        // load saved position and progress.
        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt(TIMER_STATE_POSITION)
            val progress = savedInstanceState.getLong(TIMER_STATE_PROGRESS)
            val wasPlaying = savedInstanceState.getBoolean(TIMER_STATE_PLAYING)

            currentProgress = Pair(position, progress)

            if (wasPlaying) {
                play()
            } else {
                pause()
            }
        } else {
            play()
        }
    }

    override fun onResume() {
        super.onResume()

        val item = workout.items[currentProgress.first]
        workoutProgressTitle.text = item.title
        workoutProgressStatus.text = (item.length - currentProgress.second).toTimerFormat()
    }

    override fun onPause() {
        super.onPause()

        currentTimer?.cancel()
        currentTimer = null
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        savedInstanceState.putInt(TIMER_STATE_POSITION, currentProgress.first)
        savedInstanceState.putLong(TIMER_STATE_PROGRESS, currentProgress.second)
        savedInstanceState.putBoolean(TIMER_STATE_PLAYING, playing)

        super.onSaveInstanceState(savedInstanceState)
    }

    private fun play() {
        resetBtn.visibility = Button.GONE // hide reset button.
        scheduler(position = currentProgress.first, progress = currentProgress.second) // resume/start
        playing = true
    }

    private fun pause() {
        // pause the timer.
        currentTimer?.cancel()
        currentTimer = null

        resetBtn.visibility = Button.VISIBLE // show reset button.
        playing = false
    }

    private fun scheduler(position: Int, progress: Long = 0) {

        if (position >= workout.items.size) {
            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()
            finish() // workout completed, exit activity.
        } else {
            val item = workout.items[position]

            // save progress.
            currentProgress = Pair(position, progress)

            // set title.
            workoutProgressTitle.text = item.title

            // finally if there is a next item scroll to it.
            if (position + 1 <= workout.items.size) {
                // scroll to item if the view is scrollable
                val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                    override fun getVerticalSnapPreference(): Int {
                        return LinearSmoothScroller.SNAP_TO_START
                    }

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                        return super.calculateSpeedPerPixel(displayMetrics) * 3
                    }
                }

                smoothScroller.targetPosition = position + 1

                (recyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)
            }

            if (item.length == 0L) {

                currentTimer?.cancel()
                currentTimer = null

                workoutProgressStatus.text = "Tap to continue"
                pauseBtn.isEnabled = false

                workoutProgress.setOnClickListener {
                    // clear click listener.
                    it.setOnClickListener(null)
                    pauseBtn.isEnabled = true

                    scheduler(position + 1)
                }

            } else {
                workoutProgressStatus.text = (item.length - progress).toTimerFormat()

                currentTimer = object : CountDownTimer((item.length - progress), 250) {

                    override fun onTick(millisUntilFinished: Long) {

                        // on the last 3 seconds play beep.
                        if (currentProgress.second >= (item.length - 4000L) && currentProgress.second % 1000L == 0L) {
                            if (currentProgress.second < (item.length - 1000L)) {
                                sounds?.first?.start()
                            } else {
                                sounds?.second?.start()
                            }
                        }

                        // increment progress by 250 ms.
                        currentProgress = Pair(position, currentProgress.second + 250)
                        workoutProgressStatus.text = (item.length - currentProgress.second).toTimerFormat()
                    }

                    override fun onFinish() {

                        // execute next timer/exercise.
                        scheduler(position + 1)
                    }
                }.start()
            }
        }
    }
}
