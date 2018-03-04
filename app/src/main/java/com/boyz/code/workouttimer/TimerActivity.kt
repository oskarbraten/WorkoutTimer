package com.boyz.code.workouttimer

import android.content.ComponentName
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.ExerciseAdapter
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.widget.LinearSmoothScroller
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.boyz.code.workouttimer.misc.WorkoutManager
import com.boyz.code.workouttimer.misc.toTimerFormat
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    var isBound = false

    private var onBackPressedListener: (() -> Unit)? = null
    private fun setOnBackPressedListener(listener: (() -> Unit)) {
        onBackPressedListener = listener
    }

    //private var sounds: Pair<MediaPlayer, MediaPlayer>? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            val timerService = binder.getService()
            isBound = true

            val workout = timerService!!.workout!!

            title = workout.title

            var recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercise)

            recyclerView.layoutManager = LinearLayoutManager(this@TimerActivity, LinearLayout.VERTICAL, false)
            recyclerView.adapter = ExerciseAdapter(workout)

            resetBtn.setOnLongClickListener {
                timerService.stopTimer(false)
                true
            }

            pauseBtn.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> {
                        timerService.pauseTimer()
                        resetBtn.visibility = View.VISIBLE
                    }
                    else -> {
                        timerService.resumeTimer()
                        resetBtn.visibility = View.GONE
                    }
                }
            }

            workout.items[timerService.currentProgress.first].run {
                if (timerService.isPaused() && this.length == 0L) { // paused on a non-timed exercise.
                    workoutProgressStatus.text = "Tap to continue"

                    pauseBtn.isEnabled = false

                    workoutProgress.setOnClickListener {
                        // clear click listener.
                        it.setOnClickListener(null)
                        pauseBtn.isEnabled = true

                        timerService.resumeTimer(1)
                    }
                } else if (timerService.isPaused()) {
                    workoutProgressTitle.text = this.title
                    workoutProgressStatus.text = (this.length - timerService.currentProgress.second).toTimerFormat()

                    if (timerService.isPaused()) {
                        pauseBtn.isChecked = true
                    }
                }
            }

            timerService.setOnExerciseChanged { exercise, currentProgress ->
                if (exercise.length == 0L) { // paused on a non-timed exercise.
                    workoutProgressStatus.text = "Tap to continue"

                    pauseBtn.isEnabled = false

                    workoutProgress.setOnClickListener {
                        // clear click listener.
                        it.setOnClickListener(null)
                        pauseBtn.isEnabled = true

                        timerService.resumeTimer(1)
                    }

                } else { // current state is either paused or playing, so just set current item anyways.
                    workoutProgressTitle.text = exercise.title
                    workoutProgressStatus.text = (exercise.length - timerService.currentProgress.second).toTimerFormat()

                    if (timerService.isPaused()) {
                        pauseBtn.isChecked = true
                    }
                }

                // finally if there is a next item scroll to it.
                if (currentProgress.first + 1 <= workout.items.size) {
                    // scroll to item if the view is scrollable
                    val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                        override fun getVerticalSnapPreference(): Int {
                            return LinearSmoothScroller.SNAP_TO_START
                        }

                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                            return super.calculateSpeedPerPixel(displayMetrics) * 3
                        }
                    }

                    smoothScroller.targetPosition = currentProgress.first + 1

                    (recyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)
                }
            }

            timerService.setOnTickListener { currentProgress ->

                val item = workout.items[currentProgress.first]

                workoutProgressTitle.text = item.title
                workoutProgressStatus.text = (item.length - currentProgress.second).toTimerFormat()

            }

            timerService.setOnWorkoutStopped { completed, isBound ->
                if (completed && isBound) {
                    Toast.makeText(this@TimerActivity, "Workout complete!", Toast.LENGTH_SHORT).show()
                }
                finish()
            }

            var backToast: Toast? = null
            setOnBackPressedListener {
                if (backToast != null && backToast?.view?.windowToken != null) {
                    timerService.stopTimer(false)
                } else {
                    backToast = Toast.makeText(this@TimerActivity, "Press back to stop workout", Toast.LENGTH_SHORT).apply { show() }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        supportActionBar?.hide()

        // set title and status while we're waiting for the service to respond.
        intent.getStringExtra("title")?.also { title ->
            WorkoutManager.getWorkout(this, title).items[0].also {
                workoutProgressTitle.text = it.title
                workoutProgressStatus.text = when (it.length) {
                    0L -> "Tap to continue"
                    else -> it.length.toTimerFormat()
                }
            }
        }

        // send start signal to service to make sure it is running or start it.
        startService(Intent(this@TimerActivity, TimerService::class.java))


        // initialize media player.
//        Thread({
//            val shortBeep = MediaPlayer()
//            shortBeep.setAudioAttributes(AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build())
//
//            shortBeep.setDataSource(this, Uri.parse("android.resource://com.boyz.code.workouttimer/" + R.raw.beep))
//
//            shortBeep.setVolume(0.25f, 0.25f)
//            shortBeep.prepare()
//
//            val longBeep = MediaPlayer()
//            longBeep.setAudioAttributes(AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build())
//
//            longBeep.setDataSource(this, Uri.parse("android.resource://com.boyz.code.workouttimer/" + R.raw.beep_long))
//            longBeep.setVolume(0.35f, 0.35f)
//            longBeep.prepare()
//
//            sounds = Pair(shortBeep, longBeep)
//        }).run()
    }

    override fun onResume() {
        super.onResume()

        if (!isBound) {
            bindService(Intent(this@TimerActivity, TimerService::class.java).apply {
                putExtra("title", intent.getStringExtra("title")) // title will be sent if it exists.
            }, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onBackPressed() {
        onBackPressedListener?.invoke()
    }
}