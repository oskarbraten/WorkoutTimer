package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.misc.WorkoutAdapter
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.fragment.AddWorkoutDialogFragment
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class OverviewActivity : Activity() {

    private lateinit var workouts: ArrayList<Workout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        workouts = ArrayList(WorkoutManager.getWorkouts(this))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = WorkoutAdapter(workouts)
        recyclerView.adapter = adapter

        addWorkoutBtn.setOnClickListener {
            addWorkout()
            adapter.notifyDataSetChanged()
        }
    }

    private fun addWorkout() {

        val addWorkoutDialogFragment = AddWorkoutDialogFragment()

        addWorkoutDialogFragment.show(fragmentManager, "addWorkoutDialog")

        addWorkoutDialogFragment.onConfirmedListener = { title: String, description: String ->
            val items = ArrayList<Exercise>()
            items.add(Exercise("Plank", 15000))
            items.add(Exercise("Pause", 15000))
            items.add(Exercise("Push-ups", 0))

            val workout = Workout(title, items)

            workouts.add(workout)

            WorkoutManager.addWorkout(this@OverviewActivity, workout)
        }
    }
}
