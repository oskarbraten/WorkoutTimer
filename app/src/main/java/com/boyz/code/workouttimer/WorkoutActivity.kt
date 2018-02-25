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
import android.widget.Toast
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.misc.*


class WorkoutActivity : Activity() {

    private val exercises = ArrayList<Exercise>()

    private var currentTimer: CountDownTimer? = null
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val title = intent.getStringExtra("title")
        setTitle("Workout: " + title)

        exercises.addAll(WorkoutManager.getWorkout(this, title).items)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.itemAnimator = NoAnimationItemAnimator()
        recyclerView.adapter = ExerciseAdapter(exercises)

        startBtn.setOnClickListener {
            startBtn.isEnabled = false

            recyclerView.smoothScrollToPosition(0)

            scheduler(recyclerView, position = 0)
        }

        resetBtn.setOnClickListener {
            currentTimer?.cancel()

            reset();

            recyclerView.adapter.notifyDataSetChanged()

            startBtn.isEnabled = true

            currentPosition = 0
        }
    }


    private fun reset() {
        // Loop over all exercises and reset progress.
        exercises.forEach {
            it.progress = 0
        }
    }

    private fun scheduler(recyclerView: RecyclerView, position: Int) {

        if (position >= exercises.size) {
            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()

            reset()

            recyclerView.adapter.notifyDataSetChanged()

        } else {
            val item = exercises[position]

            if (item.length == 0L) {

                progressBar.setOnClickListener {

                    // clear click listener.
                    it.setOnClickListener(null)
                    scheduler(recyclerView, position + 1)
                }

            } else {

                val oldProgress = item.progress

                currentTimer = object : CountDownTimer((item.length - oldProgress), 250) {

                    override fun onTick(millisUntilFinished: Long) {
                        item.progress += 250

                        recyclerView.adapter.notifyItemChanged(position)

                        currentPosition = position
                    }

                    override fun onFinish() {

                        // execute next timer/exercise.
                        scheduler(recyclerView, position + 1)
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
                intent.putExtra("title", this.intent.getStringExtra("title"))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
