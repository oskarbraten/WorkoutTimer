package com.boyz.code.workouttimer.misc

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.WorkoutActivity
import com.boyz.code.workouttimer.data.Workout
import kotlinx.android.synthetic.main.card_workout.view.*

class WorkoutAdapter(val workoutList: ArrayList<Workout>): RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.cardWorkoutTitle?.text = workoutList[position].title
        holder?.cardWorkoutTime?.text = workoutList[position].length().toTimerFormat()

        holder?.addListener()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_workout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardWorkoutTitle = itemView.cardWorkoutTitle!!
        val cardWorkoutTime = itemView.cardWorkoutTime!!

        fun addListener() {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, WorkoutActivity::class.java)
                intent.putExtra("title", itemView.cardWorkoutTitle.text.toString())
                itemView.context.startActivity(intent)
            }
        }
    }

}