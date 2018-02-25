package com.boyz.code.workouttimer.misc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Exercise
import kotlinx.android.synthetic.main.card_exercise.view.*

class ExerciseAdapter(val exercises: ArrayList<Exercise>): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val exercise = exercises[position]

        holder?.title?.text = exercise.title

        // reset
        holder?.status?.visibility = TextView.VISIBLE
        holder?.itemView?.setOnClickListener(null)

        if (exercise.length == 0L) {
            // hide status on exercises that are not timed.
            holder?.status?.visibility = TextView.GONE
        } else if (exercise.progress == 0L) {
            holder?.status?.text = exercise.length.toTimerFormat()
        } else {
            holder?.status?.text = (exercise.length - exercise.progress).toTimerFormat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_exercise, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.exerciseCardTitle!!
        val status = itemView.exerciseCardStatus!!
    }

}