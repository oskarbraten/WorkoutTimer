package com.boyz.code.workouttimer.misc

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.WorkoutEditActivity
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.fragment.EditExerciseFragment
import kotlinx.android.synthetic.main.card_exercise_edit.view.*

class ExerciseEditAdapter(val exerciseList: ArrayList<Exercise>): RecyclerView.Adapter<ExerciseEditAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder?.title?.text = exerciseList[position].title
        holder?.length?.visibility = TextView.VISIBLE

        holder?.itemView?.setOnClickListener(null)

        if (exerciseList[position].length == 0L) {
            holder?.length?.text = "Tap to continue"
            holder?.length?.visibility = TextView.GONE
        } else {
            holder?.length?.text = exerciseList[position].length.toTimerFormat()
        }

        holder?.moveUpBtn?.setOnClickListener {
            if (position > 0) {
                val exercise = exerciseList[position]
                exerciseList.removeAt(position)
                exerciseList.add(position - 1, exercise)
                notifyDataSetChanged()
            }
        }

        holder?.moveDownBtn?.setOnClickListener {
            if (position < itemCount - 1) {
                val exercise = exerciseList[position]
                exerciseList.removeAt(position)
                exerciseList.add(position + 1, exercise)
                notifyDataSetChanged()
            }
        }

        holder?.optionBtn?.setOnClickListener {
            EditExerciseFragment.show(it.context as Activity, exerciseList[position].title, exerciseList[position].length.toTimerInputFormat())
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_exercise_edit, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.exerciseEditCardTitle!!
        val length = itemView.exerciseEditCardLength!!
        val moveUpBtn = itemView.exerciseMoveUpBtn!!
        val moveDownBtn = itemView.exerciseMoveDownBtn!!
        val optionBtn = itemView.exerciseOptionBtn!!
    }

}