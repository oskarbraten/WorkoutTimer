package com.boyz.code.workouttimer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.ExerciseEditAdapter
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_workout_edit.*
import kotlinx.android.synthetic.main.add_exercise_dialog.*
import kotlinx.android.synthetic.main.add_exercise_dialog.view.*
import kotlinx.android.synthetic.main.add_workout_dialog.view.*

class WorkoutEditActivity : Activity() {

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_edit)

        val workoutTitle = intent.getStringExtra("title")

        title = "Workout: " + workoutTitle

        workout = WorkoutManager.getWorkout(this, workoutTitle)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkoutEdit)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        recyclerView.adapter = ExerciseEditAdapter(workout)

        deleteWorkoutBtn.setOnClickListener {
            val inf = LayoutInflater.from(this)
            val promptsView = inf.inflate(R.layout.delete_confirmation_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(this)

            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
                WorkoutManager.deleteWorkout(this, workoutTitle)
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

            promptsView.minutesInput.setOnFocusChangeListener { view, b ->
                if (!b && !promptsView.minutesInput.text.isNullOrEmpty()) {
                    when (promptsView.minutesInput.text.toString().toInt()) {
                        in 0..9 -> promptsView.minutesInput.setText("0" + promptsView.minutesInput.text.toString())
                    }
                }
            }

            promptsView.secondsInput.setOnFocusChangeListener { view, b ->
                if (!b && !promptsView.secondsInput.text.isNullOrEmpty()) {
                    when (promptsView.secondsInput.text.toString().toInt()) {
                        in 0..9 -> promptsView.secondsInput.setText("0" + promptsView.secondsInput.text.toString())
                    }
                }
            }

            val exerciseTitleInput = promptsView.exerciseTitleInput
            val minutesInput = promptsView.minutesInput
            val secondsInput = promptsView.secondsInput

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
                    val newWorkout = Workout(workoutTitle, workout.items)

                    WorkoutManager.overwriteWorkout(this, newWorkout)
                }
            })

            alertDialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "jaja, sånn kan det gå", Toast.LENGTH_LONG).show()
            })

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

            fun isInputsValid(): Boolean {
                if (promptsView.includeTimerSwitcher.isChecked) {
                    return (!promptsView.exerciseTitleInput.text.toString().equals(""))
                            && (((!minutesInput.text.toString().equals(""))
                            && (!minutesInput.text.toString().equals("00"))
                            && ((!minutesInput.text.toString().equals("0"))))
                            || ((!secondsInput.text.toString().equals(""))
                            && (!secondsInput.text.toString().equals("00"))
                            && ((!secondsInput.text.toString().equals("0")))))
                } else {
                    return (!promptsView.exerciseTitleInput.text.toString().equals(""))
                }
            }

            promptsView.includeTimerSwitcher.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    promptsView.durationWrapper.visibility = LinearLayout.VISIBLE
                } else {
                    promptsView.durationWrapper.visibility = LinearLayout.GONE
                }

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
            }

            promptsView.exerciseTitleInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
                }
            })

            promptsView.minutesInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0?.length!! > 1) {
                        promptsView.secondsInput.requestFocus()
                    }

                    if (p0.toString().isNullOrEmpty()) {
                        promptsView.minutesInput.setText("00")
                    }

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
                }
            })

            promptsView.secondsInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isNullOrEmpty()) {
                        promptsView.minutesInput.requestFocus()
                        promptsView.secondsInput.setText("00")
                    }

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
                }
            })
        }
    }

    //Legg til i utils
    fun timeInputConverter(min: Int, sec: Int): Long = ((min * 60000) + (sec * 1000)).toLong()

//    fun isInputsValid(): Boolean = (!exerciseTitleInput.text.toString().equals("")) && (!minutesInput.text.toString().equals("00")) && ((!minutesInput.text.toString().equals("0"))) && (!secondsInput.text.toString().equals("00")) && ((!secondsInput.text.toString().equals("0")))
}
