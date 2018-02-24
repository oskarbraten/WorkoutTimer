package com.boyz.code.workouttimer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.Workout
import com.boyz.code.workouttimer.misc.WorkoutItem
import com.boyz.code.workouttimer.misc.WorkoutItemAdapter
import com.boyz.code.workouttimer.misc.WorkoutManager
import com.google.gson.Gson

class WorkoutActivity : Activity() {

    private val workoutItems = ArrayList<WorkoutItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkoutItem)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        
        val title = intent.getStringExtra("title")
        workoutItems += WorkoutManager.getWorkout(this, title).items
        setTitle("Workout: " + title)

        var adapter = WorkoutItemAdapter(workoutItems)
        rv.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.workout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.workoutMenuOptions -> {
                startActivity(Intent(this, WorkoutEditActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
