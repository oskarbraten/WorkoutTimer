package com.boyz.code.workouttimer.misc

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Exercise
import kotlinx.android.synthetic.main.card_exercise.view.*

class ExerciseAdapter(val exerciseList: ArrayList<Exercise>): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val exercise = exerciseList[position]

        Log.d("DEBUG  TESTING:", exercise.toString())

        holder?.title?.text = exercise.title
        holder?.length?.visibility = TextView.VISIBLE

        holder?.itemView?.setOnClickListener(null)

        if (exercise.length == 0L) {
            holder?.length?.text = "Tap to continue"
            holder?.length?.visibility = TextView.GONE
        } else if (exercise.progress == 0L) {
            holder?.length?.text = exercise.length.toTimerFormat()
        } else {
            holder?.length?.text = (exercise.length - exercise.progress).toTimerFormat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_exercise, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.exerciseCardTitle!!
        val length = itemView.exerciseCardLength!!
    }

}