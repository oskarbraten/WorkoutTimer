package com.boyz.code.workouttimer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_workout.*
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.*


class WorkoutActivity : Activity() {

    private lateinit var workout: Workout
    private lateinit var recyclerView: RecyclerView
    private var currentTimer: CountDownTimer? = null
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val title = intent.getStringExtra("title")
        setTitle("Workout: " + title)

        workout = WorkoutManager.getWorkout(this, title)

        recyclerView = findViewById(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.itemAnimator = NoAnimationItemAnimator()
        recyclerView.adapter = ExerciseAdapter(workout.items)

        actionBtn.setOnClickListener {
            workoutProgress.visibility = LinearLayout.VISIBLE

            recyclerView.smoothScrollToPosition(0)
            scheduler(position = 0)
        }
    }

    private fun scheduler(position: Int) {

        if (position >= workout.items.size) {
            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()

            workout.reset()
            workoutProgress.visibility = LinearLayout.GONE

            recyclerView.adapter.notifyDataSetChanged()

        } else {
            val item = workout.items[position]

            workoutProgressTitle.text = item.title

            if (item.length == 0L) {

                workoutProgressStatus.text = "Tap to continue"

                workoutProgress.setOnClickListener {

                    // clear click listener.
                    it.setOnClickListener(null)
                    scheduler(position + 1)
                }
            } else {
                workoutProgressStatus.text = (item.length - item.progress).toTimerFormat()

                currentTimer = object : CountDownTimer((item.length - item.progress), 250) {

                    override fun onTick(millisUntilFinished: Long) {
                        item.progress += 250

                        workoutProgressStatus.text = (item.length - item.progress).toTimerFormat()

                        recyclerView.adapter.notifyItemChanged(position)

                        currentPosition = position
                    }

                    override fun onFinish() {

                        // execute next timer/exercise.
                        scheduler(position + 1)
                    }
                }.start()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.workout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.workoutMenuOptions -> {
                val intent = Intent(this, WorkoutEditActivity::class.java)
                intent.putExtra("title", workout.title)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
