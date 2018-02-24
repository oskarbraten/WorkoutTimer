package com.boyz.code.workouttimer

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.WorkoutItem
import com.boyz.code.workouttimer.misc.WorkoutItemAdapter

class WorkoutItemActivity : Activity() {

    private val workoutItems = ArrayList<WorkoutItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_item)

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkoutItem)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        workoutItems.add(WorkoutItem("PushUps", 600))

        var adapter = WorkoutItemAdapter(workoutItems)
        rv.adapter = adapter
    }
}
