package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_workout.*
import android.os.CountDownTimer
import android.widget.Toast
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.*
import android.support.v7.widget.LinearSmoothScroller
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.Button
import android.media.AudioAttributes
import android.net.Uri
import android.support.v7.widget.helper.ItemTouchHelper
import com.boyz.code.workouttimer.fragment.AddExerciseDialogFragment
import com.boyz.code.workouttimer.fragment.EditExerciseDialogFragment
import java.util.*


class WorkoutActivity : Activity() {

    private val STATE_POSITION = "POSITION"
    private val STATE_PROGRESS = "PROGRESS"

    private lateinit var workout: Workout
    private lateinit var recyclerView: RecyclerView
    private var sounds: Pair<MediaPlayer, MediaPlayer>? = null

    private var currentTimer: CountDownTimer? = null
    private var currentProgress: Pair<Int, Long> = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val title = intent.getStringExtra("title")
        setTitle(title)

        // load saved position and progress.
        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt(STATE_POSITION)
            val progress = savedInstanceState.getLong(STATE_PROGRESS)

            currentProgress = Pair(position, progress)

            actionBtn.isEnabled = true
        }

        workout = WorkoutManager.getWorkout(this, title)

        recyclerView = findViewById(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = ExerciseAdapter(workout)

        // add edit dialog.
        (recyclerView.adapter as ExerciseAdapter).setOnItemClickListener { _, position ->
            if (currentTimer == null) {
                val exercise = workout.items[position]

                val editExerciseFragment = EditExerciseDialogFragment.create(exercise.title, exercise.length.toTimerInputFormat())

                editExerciseFragment.show(fragmentManager, "EditExerciseDialog")

                editExerciseFragment.onConfirmedListener = { exercise ->
                    workout.items[position] = exercise
                    recyclerView.adapter.notifyDataSetChanged()

                    WorkoutManager.overwriteWorkout(this, workout)
                }

                editExerciseFragment.onDeleteListener = {
                    workout.items.removeAt(position)
                    recyclerView.adapter.notifyDataSetChanged()

                    WorkoutManager.overwriteWorkout(this, workout)

                    editExerciseFragment.dismiss()
                }
            }
        }

        // enable drag and drop reordering.
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun onMove(recyclerView: RecyclerView, from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder) : Boolean {
                if (currentTimer == null) {
                    Collections.swap(workout.items, from.adapterPosition, to.adapterPosition)

                    val newWorkout = Workout(workout.title, workout.items, workout.description)
                    WorkoutManager.overwriteWorkout(this@WorkoutActivity, newWorkout)

                    recyclerView.adapter.notifyItemMoved(from.adapterPosition, to.adapterPosition)
                }
                return true
            }

            override fun onSwiped(viewHolder:RecyclerView.ViewHolder, direction:Int) {
                //TODO: Add delete with swipe?
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            override fun getMovementFlags(recyclerView:RecyclerView, viewHolder:RecyclerView.ViewHolder):Int {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN or ItemTouchHelper.UP) //  or ItemTouchHelper.START or ItemTouchHelper.END
            }

        }).apply { attachToRecyclerView(recyclerView) }

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

        actionBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    workoutProgress.visibility = LinearLayout.VISIBLE
                    resetBtn.visibility = Button.GONE // hide reset button.
                    scheduler(position = currentProgress.first, progress = currentProgress.second) // resume/start

                    actionBar.hide()
                }
                else -> {
                    // playing --> pause.
                    currentTimer?.cancel()
                    currentTimer = null

                    resetBtn.visibility = Button.VISIBLE // show reset button.

                    if (currentProgress.first == 0 && currentProgress.second == 0L) {
                        actionBar.show()
                    }
                }
            }
        }

        resetBtn.setOnLongClickListener {
            // reset progress.
            currentProgress = Pair(0, 0)
            workoutProgress.visibility = LinearLayout.GONE // hide progress.
            resetBtn.visibility = Button.GONE // hide reset button.
            actionBar.show()
            true
        }
    }

    override fun onResume() {
        super.onResume()

        // reload workouts.
        workout.items.clear()
        workout.items.addAll(WorkoutManager.getWorkout(this, workout.title).items)

        // update view.
        recyclerView.adapter.notifyDataSetChanged()
    }

    private fun scheduler(position: Int, progress: Long = 0) {

        if (position >= workout.items.size) {
            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()
            workoutProgress.visibility = LinearLayout.GONE
            currentProgress = Pair(0, 0)

            // clear timer.
            currentTimer = null
            actionBtn.isChecked = false
            resetBtn.visibility = Button.GONE // hide reset button.
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
                actionBtn.isEnabled = false

                workoutProgress.setOnClickListener {
                    // clear click listener.
                    it.setOnClickListener(null)
                    actionBtn.isEnabled = true

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

    override fun onDestroy() {
        super.onDestroy()

        currentTimer?.cancel()
        currentTimer = null
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(STATE_POSITION, currentProgress.first)
        savedInstanceState.putLong(STATE_PROGRESS, currentProgress.second)

        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_workout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.workoutMenuAdd -> {
                val addExerciseDialogFragment = AddExerciseDialogFragment()

                addExerciseDialogFragment.show(fragmentManager, "AddExerciseDialog")

                addExerciseDialogFragment.onConfirmedListener = { exercise ->

                    workout.items.add(exercise)

                    val newWorkout = Workout(workout.title, workout.items, workout.description)

                    WorkoutManager.overwriteWorkout(this, newWorkout)
                }
                true
            }
            R.id.workoutMenuDelete -> {
                val inf = LayoutInflater.from(this)
                val promptsView = inf.inflate(R.layout.dialog_delete_confirmation, null)
                val alertDialogBuilder = AlertDialog.Builder(this)

                alertDialogBuilder.setTitle("Delete this workout?")
                alertDialogBuilder.setView(promptsView)

                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton("Delete", { dialogInterface: DialogInterface, i: Int ->
                    WorkoutManager.deleteWorkout(this, workout.title)
                    finish()
                    Toast.makeText(this, "Workout deleted!", Toast.LENGTH_LONG).show()
                })

                alertDialogBuilder.setNegativeButton(android.R.string.cancel, { dialogInterface: DialogInterface, i: Int ->
                    // canceled
                })

                val alertDialog = alertDialogBuilder.create()

                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
