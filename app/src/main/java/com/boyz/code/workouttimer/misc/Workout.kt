package com.boyz.code.workouttimer.misc

data class WorkoutItem(var title: String, var length: Int)

data class Workout(var title: String, val items : ArrayList<WorkoutItem>) {
    fun length(): Int {
        return items.fold(0) { total, next -> total + next.length }
    }
    fun convertLength(): String {

        val total = length()

        val hours = (total / (60*60) % 24)
        val minutes = (total / (60) % 60)
        val seconds = (total % 60)

        val cHours = when (hours) {
            in 0..9 -> "0" + hours.toString()
            else -> hours.toString()
        }

        val cMinutes = when (minutes) {
            in 0..9 -> "0" + minutes.toString()
            else -> minutes.toString()
        }

        val cSeconds = when (seconds) {
            in 0..9 -> "0" + seconds.toString()
            else -> seconds.toString()
        }

        return "$cHours:$cMinutes:$cSeconds"
    }
}