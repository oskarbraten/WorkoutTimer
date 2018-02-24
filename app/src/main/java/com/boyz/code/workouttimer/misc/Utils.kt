package com.boyz.code.workouttimer.misc

import android.content.Context
import com.google.gson.Gson

fun Int?.convertLength(): String {

    val total = this!!

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

object WorkoutManager {

    fun getWorkouts(context: Context): List<Workout> {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE).all.map {(key, value) ->
            val workout = Gson().fromJson("$value", Workout::class.java)
            Workout(key, workout.items)
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
}

