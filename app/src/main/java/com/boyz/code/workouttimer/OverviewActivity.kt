package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.misc.WorkoutAdapter
import com.boyz.code.workouttimer.misc.Workout
import com.boyz.code.workouttimer.misc.Exercise
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class OverviewActivity : Activity() {

    private val workouts = ArrayList<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        workouts += WorkoutManager.getWorkouts(this);

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = WorkoutAdapter(workouts)
        rv.adapter = adapter

        addWorkoutButton.setOnClickListener {
            addWorkout()
            adapter.notifyDataSetChanged()
        }
    }

    private fun addWorkout() {
        val inf = LayoutInflater.from(this)
        val promptsView = inf.inflate(R.layout.add_workout_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setView(promptsView)

        val workoutTitleInput = promptsView.workoutTitleInput

        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
            val workoutItems = ArrayList<Exercise>()
            workoutItems.add(Exercise("Plank", 5))
            workoutItems.add(Exercise("Reverse plank", 5))
            workoutItems.add(Exercise("Push-ups", 0))
            workoutItems.add(Exercise("Planche", 5))
            workoutItems.add(Exercise("Push-downs", 0))
            workoutItems.add(Exercise("Plank", 5))
            workoutItems.add(Exercise("Reverse plank", 5))
            workoutItems.add(Exercise("Push-ups", 0))
            workoutItems.add(Exercise("Planche", 5))
            workoutItems.add(Exercise("Push-downs", 0))
            workoutItems.add(Exercise("Plank", 5))
            workoutItems.add(Exercise("Reverse plank", 5))
            workoutItems.add(Exercise("Push-ups", 0))
            workoutItems.add(Exercise("Planche", 5))
            workoutItems.add(Exercise("Push-downs", 0))

            workoutItems.add(Exercise("Pause", 5))
            val workout = Workout(workoutTitleInput.text.toString(), workoutItems)
            workouts.add(workout)
            WorkoutManager.addWorkout(this, workout)
        })

        alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
        })

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }
}
