package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.ExerciseEditAdapter
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_workout_edit.*
import kotlinx.android.synthetic.main.add_exercise_dialog.view.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class WorkoutEditActivity : Activity() {

    private val exercises = ArrayList<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_edit)

        val exerciseTitle = intent.getStringExtra("title")

        title = "Workout: " + exerciseTitle

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkoutEdit)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        exercises += WorkoutManager.getWorkout(this, exerciseTitle).items

        recyclerView.adapter = ExerciseEditAdapter(exercises)

        deleteWorkoutBtn.setOnClickListener {
            val inf = LayoutInflater.from(this)
            val promptsView = inf.inflate(R.layout.delete_confirmation_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(this)

            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
                WorkoutManager.deleteWorkout(this, exerciseTitle)
                finish()
                startActivity(Intent(this, OverviewActivity::class.java))
                Toast.makeText(this, "Workout deleted!", Toast.LENGTH_LONG).show()
            })

            alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
            })

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }

        addExerciseButton.setOnClickListener {
            val inf = LayoutInflater.from(this)
            val promptsView = inf.inflate(R.layout.add_exercise_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(this)

            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
                WorkoutManager.deleteWorkout(this, exerciseTitle)
                finish()
                startActivity(Intent(this, OverviewActivity::class.java))
                Toast.makeText(this, "Workout deleted!", Toast.LENGTH_LONG).show()
            })

            alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
            })

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }
    }
}
