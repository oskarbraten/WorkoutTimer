package com.boyz.code.workouttimer

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.WorkoutAdapter
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.fragment.AddWorkoutDialogFragment
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : Activity() {

    private lateinit var workouts: ArrayList<Workout>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        workouts = ArrayList(WorkoutManager.getWorkouts(this).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }))

        recyclerView = findViewById(R.id.recyclerViewWorkout)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = WorkoutAdapter(workouts)

        addWorkoutBtn.setOnClickListener {
            val addWorkoutDialogFragment = AddWorkoutDialogFragment()

            addWorkoutDialogFragment.show(fragmentManager, "addWorkoutDialog")

            addWorkoutDialogFragment.onConfirmedListener = { title: String, description: String ->
                val items = ArrayList<Exercise>()
                items.add(Exercise("Plank", 15000))
                items.add(Exercise("Pause", 15000))
                items.add(Exercise("Push-ups", 0))

                val workout = Workout(title, items, description)

                workouts.add(workout)

                workouts.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

                WorkoutManager.addWorkout(this@OverviewActivity, workout)

                recyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        workouts.clear()
        workouts.addAll(ArrayList(WorkoutManager.getWorkouts(this).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })))
        recyclerView.adapter.notifyDataSetChanged()
    }
}
