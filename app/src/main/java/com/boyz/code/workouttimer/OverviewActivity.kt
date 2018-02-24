package com.boyz.code.workouttimer

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.CustomAdapter
import com.boyz.code.workouttimer.misc.Workout
import com.boyz.code.workouttimer.misc.WorkoutItem
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val workouts = ArrayList<Workout>()

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = CustomAdapter(workouts)
        rv.adapter = adapter

        addWorkoutButton.setOnClickListener {
            val workoutItems = ArrayList<WorkoutItem>()
            workoutItems.add(WorkoutItem("PushUps", 10000))
            workoutItems.add(WorkoutItem("Pause", 10000))
            workouts.add(Workout("Test", workoutItems))
            adapter.notifyDataSetChanged()
        }
    }
}
