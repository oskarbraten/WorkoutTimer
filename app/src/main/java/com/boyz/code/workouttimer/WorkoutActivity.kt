package com.boyz.code.workouttimer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

//        workoutItems.add(WorkoutItem("Default", 600))
        val title = intent.getStringExtra("title")
        workoutItems += WorkoutManager.getWorkout(this, title).items

        var adapter = WorkoutItemAdapter(workoutItems)
        rv.adapter = adapter
    }
}
