package com.boyz.code.workouttimer.misc

data class WorkoutItem(var title: String, var length: Int)

data class Workout(var title: String, val items : ArrayList<WorkoutItem>) {
    fun length(): Int {
        return items.sumBy { it.length }
    }
}