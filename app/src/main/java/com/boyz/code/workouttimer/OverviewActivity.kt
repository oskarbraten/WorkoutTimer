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
import com.boyz.code.workouttimer.misc.CustomAdapter
import com.boyz.code.workouttimer.misc.Workout
import com.boyz.code.workouttimer.misc.WorkoutItem
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class OverviewActivity : Activity() {

    private val workouts = ArrayList<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = CustomAdapter(workouts)
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
            val workoutItems = ArrayList<WorkoutItem>()
            workoutItems.add(WorkoutItem("PushUps", 600))
            workoutItems.add(WorkoutItem("Pause", 600))
            workouts.add(Workout(workoutTitleInput.text.toString(), workoutItems))
        })
        alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
        })

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }
}
