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
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.*
import kotlinx.android.synthetic.main.activity_workout_edit.*
import kotlinx.android.synthetic.main.dialog_add_exercise.view.*

class WorkoutEditActivity : Activity() {

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_edit)

        val workoutTitle = intent.getStringExtra("title")

        title = "Editing: " + workoutTitle

        workout = WorkoutManager.getWorkout(this, workoutTitle)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkoutEdit)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        recyclerView.adapter = ExerciseEditAdapter(workout)

        addExerciseButton.setOnClickListener {
            val inf = LayoutInflater.from(this)
            val promptsView = inf.inflate(R.layout.dialog_add_exercise, null)
            val alertDialogBuilder = AlertDialog.Builder(this)


            alertDialogBuilder.setTitle("Add exercise")
            alertDialogBuilder.setView(promptsView)

            val exerciseTitleInput = promptsView.exerciseTitleInput
            val minutesInput = promptsView.minutesInput
            val secondsInput = promptsView.secondsInput
            val includeTimerSwitcher = promptsView.includeTimerSwitcher
            val durationWrapper = promptsView.durationWrapper

            alertDialogBuilder.setCancelable(false)

            alertDialogBuilder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
                if (exerciseTitleInput.equals("")) {
                    Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
                } else {
                    val length = when (promptsView.includeTimerSwitcher.isChecked) {
                        false -> 0
                        else -> timeInputConverter(minutesInput.text.toString().toInt(), secondsInput.text.toString().toInt())
                    }
                    val exercise = Exercise(exerciseTitleInput.text.toString(), length)

                    workout.items.add(exercise)
                    val newWorkout = Workout(workout.title, workout.items, workout.description)

                    WorkoutManager.overwriteWorkout(this, newWorkout)
                }
            })

            alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
            })

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

            setExerciseDialogValidators(exerciseTitleInput, includeTimerSwitcher, minutesInput, secondsInput, durationWrapper, alertDialog)

        }
    }

}
