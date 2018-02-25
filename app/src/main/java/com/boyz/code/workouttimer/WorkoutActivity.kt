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
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.misc.*
import kotlinx.android.synthetic.main.card_exercise.view.*


class WorkoutActivity : Activity() {

    private val exercises = ArrayList<Exercise>()
    private var currentTimer: CountDownTimer? = null
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        recyclerView.itemAnimator = NoAnimationItemAnimator()
        
        val title = intent.getStringExtra("title")

        setTitle("Workout: " + title)

        exercises += WorkoutManager.getWorkout(this, title).items

        recyclerView.adapter = ExerciseAdapter(exercises)

        startBtn.setOnClickListener { view ->
            startBtn.isEnabled = false
            pauseBtn.isEnabled = true

            recyclerView.smoothScrollToPosition(0)

            scheduler(recyclerView, position = 0)
        }

        resetBtn.setOnClickListener {
            currentTimer?.cancel()

            reset();

            recyclerView.adapter.notifyDataSetChanged()

            startBtn.isEnabled = true
            pauseBtn.isEnabled = false

            currentPosition = 0

            pauseBtn.text = "Pause"
            setPauseBtnListener(recyclerView)
        }

        setPauseBtnListener(recyclerView)
    }



    private fun setPauseBtnListener(recyclerView: RecyclerView) {
        pauseBtn.setOnClickListener {
            pauseBtn.text = "Resume"
            currentTimer?.cancel()
            pauseBtn.setOnClickListener {
                pauseBtn.text = "Pause"
                scheduler(recyclerView, currentPosition)

                setPauseBtnListener(recyclerView)
            }
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
            startBtn.isEnabled = true

            reset();

            recyclerView.adapter.notifyDataSetChanged()

        } else {
            val item = exercises[position]
            val itemView = recyclerView.layoutManager.findViewByPosition(position)
            val itemViewLength = itemView.exerciseCardLength

            if (item.length == 0L) {

                pauseBtn.isEnabled = false

                itemViewLength.visibility = TextView.VISIBLE

                recyclerView.setOnClickListener {

                    Log.d("Debug", "Clicked!!!!")
                    // clear click listener.
                    it.setOnClickListener(null)

                    itemViewLength.visibility = TextView.GONE

                    scheduler(recyclerView, position + 1)

                    pauseBtn.isEnabled = true
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
