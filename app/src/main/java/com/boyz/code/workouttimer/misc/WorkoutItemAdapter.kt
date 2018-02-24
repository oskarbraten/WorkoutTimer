package com.boyz.code.workouttimer.misc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boyz.code.workouttimer.R
import kotlinx.android.synthetic.main.card_workout_item.view.*

class WorkoutItemAdapter(val workoutItemList: ArrayList<WorkoutItem>): RecyclerView.Adapter<WorkoutItemAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.cardWorkoutItemTitle?.text = workoutItemList[position].title
        holder?.cardWorkoutItemLength?.text = workoutItemList[position].length.convertLength()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_workout_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return workoutItemList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardWorkoutItemTitle = itemView.cardWorkoutItemTitle!!
        val cardWorkoutItemLength = itemView.cardWorkoutItemLength!!
    }

}