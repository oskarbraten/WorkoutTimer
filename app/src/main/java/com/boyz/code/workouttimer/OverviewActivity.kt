package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.misc.WorkoutAdapter
import com.boyz.code.workouttimer.misc.Workout
import com.boyz.code.workouttimer.misc.WorkoutItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class OverviewActivity : Activity() {

    private val workouts = ArrayList<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        getWorkoutsFromPrefs()

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
            val workoutItems = ArrayList<WorkoutItem>()
            workoutItems.add(WorkoutItem("PushUps", 600))
            workoutItems.add(WorkoutItem("Pause", 600))
            val workout = Workout(workoutTitleInput.text.toString(), workoutItems)
            workouts.add(workout)
            saveWorkoutToPrefs(workout)
        })
        alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
        })

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    fun saveWorkoutToPrefs(workout: Workout) {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(workout)
        editor.putString(workout.title, json).apply()
    }

    fun getWorkoutsFromPrefs() {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val workoutsFromPrefs = prefs.all

        workoutsFromPrefs.forEach {(key, value) ->
            val workout = Gson().fromJson("$value", Workout::class.java)
            workouts.add(Workout(key, workout.items))
        }
    }
}
