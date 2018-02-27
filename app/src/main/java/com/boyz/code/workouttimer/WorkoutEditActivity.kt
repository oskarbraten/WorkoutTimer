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
import com.boyz.code.workouttimer.fragment.AddExerciseDialogFragment
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
            val addExerciseDialogFragment = AddExerciseDialogFragment()

            addExerciseDialogFragment.show((it.context as Activity).fragmentManager, "BadlaMeLøg")

            addExerciseDialogFragment.onConfirmedListener = { exercise ->
                workout.items.add(exercise)
                val newWorkout = Workout(workout.title, workout.items, workout.description)

                WorkoutManager.overwriteWorkout(this, newWorkout)
            }
        }
    }

}
