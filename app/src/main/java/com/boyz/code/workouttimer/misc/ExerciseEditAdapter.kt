package com.boyz.code.workouttimer.misc

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Workout
import com.boyz.code.workouttimer.fragment.EditExerciseDialogFragment
import kotlinx.android.synthetic.main.card_exercise_edit.view.*

class ExerciseEditAdapter(private val workout: Workout): RecyclerView.Adapter<ExerciseEditAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        val exerciseList = workout.items

        holder?.title?.text = exerciseList[position].title
        holder?.length?.visibility = TextView.VISIBLE

        if (exerciseList[position].length == 0L) {
            holder?.length?.text = "No timer"
//            holder?.length?.visibility = TextView.GONE
//            holder?.length?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        } else {
            holder?.length?.text = exerciseList[position].length.toTimerFormat()
        }

        holder?.moveUpBtn?.setOnClickListener {
            if (position > 0) {
                val exercise = exerciseList[position]
                exerciseList.removeAt(position)
                exerciseList.add(position - 1, exercise)
                notifyDataSetChanged()

                val newWorkout = Workout(workout.title, exerciseList, workout.description)
                WorkoutManager.overwriteWorkout(it.context, newWorkout)
            }
        }

        holder?.moveDownBtn?.setOnClickListener {
            if (position < itemCount - 1) {
                val exercise = exerciseList[position]
                exerciseList.removeAt(position)
                exerciseList.add(position + 1, exercise)
                notifyDataSetChanged()

                val newWorkout = Workout(workout.title, exerciseList, workout.description)
                WorkoutManager.overwriteWorkout(it.context, newWorkout)

            }
        }

        holder?.itemView?.setOnClickListener {
            val editExerciseFragment = EditExerciseDialogFragment.create(exerciseList[position].title, exerciseList[position].length.toTimerInputFormat())

            editExerciseFragment.show((it.context as Activity).fragmentManager, "BadlaMeLøg")

            editExerciseFragment.onConfirmedListener = {exercise ->
                exerciseList[position] = exercise
                notifyDataSetChanged()

                val newWorkout = Workout(workout.title, exerciseList, workout.description)
                WorkoutManager.overwriteWorkout(it.context, newWorkout)
            }

            editExerciseFragment.onDeleteListener = {
                exerciseList.removeAt(position)
                notifyDataSetChanged()

                val newWorkout = Workout(workout.title, exerciseList, workout.description)
                WorkoutManager.overwriteWorkout(it.context, newWorkout)

                editExerciseFragment.dismiss()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_exercise_edit, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return workout.items.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.exerciseEditCardTitle!!
        val length = itemView.exerciseEditCardLength!!
        val moveUpBtn = itemView.exerciseMoveUpBtn!!
        val moveDownBtn = itemView.exerciseMoveDownBtn!!
//        val optionBtn = itemView.exerciseOptionBtn!!
    }

}