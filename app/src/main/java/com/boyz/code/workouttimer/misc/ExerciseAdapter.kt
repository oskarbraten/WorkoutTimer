package com.boyz.code.workouttimer.misc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Workout
import kotlinx.android.synthetic.main.card_exercise.view.*

class ExerciseAdapter(private val workout: Workout) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    private var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((view: View, position: Int) -> Unit)) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var exercise = workout.items[position]

        holder?.title?.text = exercise.title

        // reset visibility
        holder?.status?.visibility = TextView.VISIBLE

        when {
            exercise.length == 0L -> {// hide status on exercises that are not timed.
                holder?.status?.visibility = TextView.GONE
            }
            exercise.progress == 0L -> {
                holder?.status?.text = exercise.length.toTimerFormat()
            }
            else -> {
                holder?.status?.text = (exercise.length - exercise.progress).toTimerFormat()
            }
        }

        holder?.itemView?.setOnClickListener {
            onItemClickListener?.invoke(it, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_exercise, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return workout.items.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.exerciseCardTitle!!
        val status = itemView.exerciseCardStatus!!
    }

}