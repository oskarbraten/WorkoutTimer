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
import android.widget.TextView
import android.widget.Toast
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.misc.*
import kotlinx.android.synthetic.main.card_exercise.view.*


class WorkoutActivity : Activity() {

    private val exercises = ArrayList<Exercise>()
    private var currentTimer: CountDownTimer? = null
    private var currentProgress: Pair<Int, Long> = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercise)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        
        val title = intent.getStringExtra("title")

        setTitle("Workout: " + title)

        exercises += WorkoutManager.getWorkout(this, title).items

        recyclerView.adapter = ExerciseAdapter(exercises)

        startBtn.setOnClickListener { view ->
            startBtn.isEnabled = false
            pauseBtn.isEnabled = true

//            recyclerView.smoothScrollToPosition(0)
//            recyclerView.disableScrolling()
//
//            recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                if (scrollY == 0) {
//                    // finally reached the top.
                    scheduler(recyclerView, position = 0)
                //}
            //}
        }

        resetBtn.setOnClickListener {
            currentTimer?.cancel()
            recyclerView.adapter.notifyDataSetChanged()

            startBtn.isEnabled = true
            pauseBtn.isEnabled = false

            currentProgress = Pair(0, 0)

            pauseBtn.text = "Pause"
            setPauseBtnListener(recyclerView)
        }

        setPauseBtnListener(recyclerView)
    }

    fun setPauseBtnListener(recyclerView: RecyclerView) {
        pauseBtn.setOnClickListener {
            pauseBtn.text = "Resume"
            currentTimer?.cancel()
            pauseBtn.setOnClickListener {
                pauseBtn.text = "Pause"
                scheduler(recyclerView, currentProgress.first, currentProgress.second)

                setPauseBtnListener(recyclerView)
            }
        }
    }

    fun scheduler(recyclerView: RecyclerView, position: Int, progressMillis: Long? = null) {

        if (position >= exercises.size) {
            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()
            startBtn.isEnabled = true
            recyclerView.adapter.notifyDataSetChanged()

        } else {
            val item = exercises.get(position)
            val itemView = recyclerView.layoutManager.findViewByPosition(position)
            val itemViewLength = itemView.exerciseCardLength

            itemView.requestFocus() // scroll to item if not in view.

            if (item.length == 0L) {

                pauseBtn.isEnabled = false

                itemView.exerciseCardLength.visibility = TextView.VISIBLE

                itemView.setOnClickListener {
                    itemViewLength.visibility = TextView.GONE
                    scheduler(recyclerView, position + 1)
                    it.setOnClickListener(null)

                    pauseBtn.isEnabled = true
                }

            } else {

                var progressMillis2 = progressMillis

                if (progressMillis2 == null) {
                    progressMillis2 = item.length * 1000
                }
                currentTimer = object : CountDownTimer(progressMillis2, 50) {

                    override fun onTick(millisUntilFinished: Long) {
                        itemViewLength.text = (Math.ceil(millisUntilFinished.toDouble() / 1000)).toLong().toTimerFormat()
                        currentProgress = Pair(position, millisUntilFinished)
                    }

                    override fun onFinish() {
                        itemViewLength.text = (0L).toTimerFormat()

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
