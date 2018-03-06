package com.boyz.code.workouttimer.misc

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Workout
import kotlinx.android.synthetic.main.card_workout.view.*

class WorkoutAdapter(private val workouts: ArrayList<Workout>): RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    private var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((view: View, position: Int) -> Unit)) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        val workout = workouts[position]

        holder?.title?.text = workout.title
        holder?.time?.text = workout.length().toTimerLongFormat()

        // reset visibility on every bind.
        holder?.description?.visibility = CardView.GONE

        if (workout.description != "") {
            holder?.description?.text = workout.description
            holder?.description?.visibility = CardView.VISIBLE
        }


        holder?.itemView?.setOnClickListener {
            onItemClickListener?.invoke(it, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_workout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title = itemView.workoutCardTitle!!
        val time = itemView.workoutCardTime!!
        val description = itemView.workoutCardDescription!!
    }
}