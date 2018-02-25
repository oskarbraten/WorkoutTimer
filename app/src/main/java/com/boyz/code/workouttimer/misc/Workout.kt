package com.boyz.code.workouttimer.misc

data class Exercise(val title: String, val length: Long, val progress: Long = 0L)

data class Workout(var title: String, val items : ArrayList<Exercise>) {
    fun length(): Long {
        return (items.sumBy { it.length.toInt() }).toLong()
    }
}