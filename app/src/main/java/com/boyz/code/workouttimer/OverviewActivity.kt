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
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class OverviewActivity : Activity() {

    private val workouts = ArrayList<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        workouts.addAll(WorkoutManager.getWorkouts(this))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = WorkoutAdapter(workouts)
        recyclerView.adapter = adapter

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
        alertDialogBuilder.setPositiveButton("OK", null)
        alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        val positiveButton: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setOnClickListener{
            val title = promptsView.workoutTitleInput.getText().toString()

            if (WorkoutManager.doesWorkoutExist(this@OverviewActivity, title)) {
                Toast.makeText(this@OverviewActivity, "Invalid data", Toast.LENGTH_SHORT).show()

            } else {
                val workoutItems = ArrayList<Exercise>()
                workoutItems.add(Exercise("Plank", 5000))
                workoutItems.add(Exercise("Reverse plank", 5000))
                workoutItems.add(Exercise("Push-ups", 0))

                val workout = Workout(workoutTitleInput.text.toString(), workoutItems)
                workouts.add(workout)
                WorkoutManager.addWorkout(this@OverviewActivity, workout)
                alertDialog.dismiss();
            }
        }


    }


}
