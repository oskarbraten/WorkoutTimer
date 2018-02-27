package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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


class WorkoutActivity : Activity() {

    private lateinit var workout: Workout
    private lateinit var recyclerView: RecyclerView

    private var currentTimer: CountDownTimer? = null
    private var currentProgress: Pair<Int, Long> = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val title = intent.getStringExtra("title")
        setTitle(title)

        workout = WorkoutManager.getWorkout(this, title)

        recyclerView = findViewById(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = ExerciseAdapter(workout.items)

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

            if (item.length == 0L) {

                workoutProgressStatus.text = "Tap to continue"
                actionBtn.isEnabled = false

                workoutProgress.setOnClickListener {
                    // clear click listener.
                    it.setOnClickListener(null)
                    scheduler(position + 1)

                    actionBtn.isEnabled = true
                }

            } else {
                workoutProgressStatus.text = (item.length - progress).toTimerFormat()

                currentTimer = object : CountDownTimer((item.length - progress), 250) {

                    override fun onTick(millisUntilFinished: Long) {
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

            // finally if there is a next item scroll to it.
            val scrollable = recyclerView.computeVerticalScrollRange() > recyclerView.height
            if (scrollable && position + 1 >= workout.items.size) {
                // scroll to item if the view is scrollable
                val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                    override fun getVerticalSnapPreference(): Int {
                        return LinearSmoothScroller.SNAP_TO_START
                    }

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                        return super.calculateSpeedPerPixel(displayMetrics) * 3
                    }
                }
                smoothScroller.targetPosition = position

                (recyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_workout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.workoutMenuEdit -> {
                val intent = Intent(this, WorkoutEditActivity::class.java)
                intent.putExtra("title", workout.title)
                startActivity(intent)
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
