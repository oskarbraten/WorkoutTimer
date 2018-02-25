package com.boyz.code.workouttimer.misc

data class WorkoutItem(val title: String, val length: Long, val progress: Long = 0L)

data class Workout(var title: String, val items : ArrayList<WorkoutItem>) {
    fun length(): Long {
        return (items.sumBy { it.length.toInt() }).toLong()
    }
}