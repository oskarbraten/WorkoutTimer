package com.boyz.code.workouttimer.misc

data class WorkoutItem(var title: String, var length: Int)

data class Workout(var title: String, val items : ArrayList<WorkoutItem>) {
    fun length(): Int {
        return items.fold(0) { total, next -> total + next.length }
    }
}