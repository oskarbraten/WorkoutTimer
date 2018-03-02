package com.boyz.code.workouttimer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_workout.*
import android.widget.Toast
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.misc.*
import android.view.LayoutInflater
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.helper.ItemTouchHelper
import com.boyz.code.workouttimer.fragment.AddExerciseDialogFragment
import com.boyz.code.workouttimer.fragment.EditExerciseDialogFragment
import kotlinx.android.synthetic.main.card_workout.view.*
import java.util.*


class WorkoutActivity : AppCompatActivity() {

    private lateinit var workout: Workout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val title = intent.getStringExtra("title")
        setTitle(title)

        workout = WorkoutManager.getWorkout(this, title)

        recyclerView = findViewById(R.id.recyclerViewExercise)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = ExerciseAdapter(workout)

        // add edit dialog.
        (recyclerView.adapter as ExerciseAdapter).setOnItemClickListener { _, position ->
            val exercise = workout.items[position]

            val editExerciseFragment = EditExerciseDialogFragment.create(exercise.title, exercise.length.toTimerInputFormat())

            editExerciseFragment.show(fragmentManager, "EditExerciseDialog")

            editExerciseFragment.onConfirmedListener = { exercise ->
                workout.items[position] = exercise
                recyclerView.adapter.notifyDataSetChanged()

                WorkoutManager.overwriteWorkout(this, workout)
            }

            editExerciseFragment.onDeleteListener = {
                workout.items.removeAt(position)
                recyclerView.adapter.notifyDataSetChanged()

                WorkoutManager.overwriteWorkout(this, workout)

                editExerciseFragment.dismiss()
            }
        }

        // enable drag and drop reordering.
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun onMove(recyclerView: RecyclerView, from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder) : Boolean {
                Collections.swap(workout.items, from.adapterPosition, to.adapterPosition)

                val newWorkout = Workout(workout.title, workout.items, workout.description)
                WorkoutManager.overwriteWorkout(this@WorkoutActivity, newWorkout)

                recyclerView.adapter.notifyItemMoved(from.adapterPosition, to.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder:RecyclerView.ViewHolder, direction:Int) {
                //TODO: Add delete with swipe?
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            override fun getMovementFlags(recyclerView:RecyclerView, viewHolder:RecyclerView.ViewHolder):Int {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN or ItemTouchHelper.UP) //  or ItemTouchHelper.START or ItemTouchHelper.END
            }

        }).apply { attachToRecyclerView(recyclerView) }

        playBtn.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("title", workout.title)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // reload workouts.
        workout.items.clear()
        workout.items.addAll(WorkoutManager.getWorkout(this, workout.title).items)

        // update view.
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_workout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.workoutMenuAdd -> {
                val addExerciseDialogFragment = AddExerciseDialogFragment()

                addExerciseDialogFragment.show(fragmentManager, "AddExerciseDialog")

                addExerciseDialogFragment.onConfirmedListener = { exercise ->

                    workout.items.add(exercise)

                    val newWorkout = Workout(workout.title, workout.items, workout.description)

                    WorkoutManager.overwriteWorkout(this, newWorkout)
                }
                true
            }
            R.id.workoutMenuDelete -> {
                val inf = LayoutInflater.from(this)
                val promptsView = inf.inflate(R.layout.dialog_delete_confirmation, null)
                val alertDialogBuilder = AlertDialog.Builder(this)

                alertDialogBuilder.setTitle("Delete this workout?")
                alertDialogBuilder.setView(promptsView)

                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton("Delete", { dialogInterface: DialogInterface, i: Int ->
                    WorkoutManager.deleteWorkout(this, workout.title)
                    finish()
                    Toast.makeText(this, "Workout deleted!", Toast.LENGTH_LONG).show()
                })

                alertDialogBuilder.setNegativeButton(android.R.string.cancel, { _, _: Int ->
                    // canceled
                })

                val alertDialog = alertDialogBuilder.create()

                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
