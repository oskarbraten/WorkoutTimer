package com.boyz.code.workouttimer.misc

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.boyz.code.workouttimer.data.Workout
import com.google.gson.Gson
import android.widget.Toast



fun Long?.toTimerFormat(): String {

    val total = this!! / 1000

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

    return when (hours) {
        0L -> "$cMinutes:$cSeconds"
        else -> "$cHours:$cMinutes:$cSeconds"
    }
}

fun Long?.toTimerLongFormat(): String {

    val total = this!! / 1000

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

    return when (hours) {
        0L -> "$cMinutes:$cSeconds"
        else -> "$cHours:$cMinutes:$cSeconds"
    }
}

fun RecyclerView.disableScrolling() {
    this.addOnItemTouchListener(RecyclerViewDisableListener())
}

fun RecyclerView.enableScrolling() {
    this.removeOnItemTouchListener(RecyclerViewDisableListener())
}

object WorkoutManager {

    fun getWorkouts(context: Context): List<Workout> {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE).all.map {(key, value) ->
            val workout = Gson().fromJson("$value", Workout::class.java)
            Workout(key, workout.items)
        }
    }



    fun doesWorkoutExist(context: Context, title: String) : Boolean {
        var exists = false

        getWorkoutTitles(context).forEach {
            if (it.equals(title)) {
                exists = true
            }
        }

        return exists
    }

    fun getWorkoutTitles(context: Context): List<String> {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE).all.map {(key, value) ->
            key
        }
    }

    fun getWorkout(context: Context, title: String) : Workout {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(title, "{message:'No json'}")
        return gson.fromJson(json, Workout::class.java)
    }

    fun addWorkout(context: Context, workout: Workout) {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(workout)
        editor.putString(workout.title, json).apply()
    }

    fun deleteWorkout(context: Context, title: String) {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.edit().putString(title, null).apply()
    }
}

