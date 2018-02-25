package com.boyz.code.workouttimer.data

data class Workout(var title: String, val items : ArrayList<Exercise>) {
    fun length(): Long {
        return (items.sumBy { (item)-> item.length.toInt() }).toLong()
    }

    fun reset() {
        items.forEach {
            it.progress = 0
        }
    }
}