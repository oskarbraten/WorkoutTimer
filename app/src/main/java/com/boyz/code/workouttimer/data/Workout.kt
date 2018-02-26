package com.boyz.code.workouttimer.data

data class Workout(var title: String, val items : ArrayList<Exercise>) {
    fun length(): Long {
        return items.fold(initial = 0L) { acc: Long, exercise: Exercise ->
            acc + exercise.length
        }
    }

    fun reset() {
        items.forEach {
            it.progress = 0
        }
    }
}