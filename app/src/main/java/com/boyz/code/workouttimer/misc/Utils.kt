package com.boyz.code.workouttimer.misc

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import com.boyz.code.workouttimer.data.Workout
import com.google.gson.Gson

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
        0L -> "${cMinutes}m ${cSeconds}s"
        else -> "${cHours}h ${cMinutes}m ${cSeconds}s"
    }
}

fun Long?.toTimerInputFormat(): String {

    val total = this!! / 1000

    var minutes = total / (60)

    val seconds = when (minutes) {
        100L -> {
            minutes = 99
            total - (99 * 60)
        }
        else -> (total % 60)
    }

    val cMinutes = when (minutes) {
        in 0..9 -> "0" + minutes.toString()
        else -> minutes.toString()
    }

    val cSeconds = when (seconds) {
        in 0..9 -> "0" + seconds.toString()
        else -> seconds.toString()
    }

    return "$cMinutes:$cSeconds"
}

fun timeInputConverter(min: Int, sec: Int): Long = ((min * 60000) + (sec * 1000)).toLong()

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
            Workout(key, workout.items, workout.description)
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

    fun overwriteWorkout(context: Context, workout: Workout) {
        addWorkout(context, workout)
    }

    fun deleteWorkout(context: Context, title: String) {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.edit().putString(title, null).apply()
    }
}



fun setExerciseDialogValidators(exerciseTitleInput: EditText, includeTimerSwitcher: Switch, minutesInput: EditText, secondsInput: EditText, durationWrapper: LinearLayout, alertDialog: AlertDialog){

    minutesInput.setOnFocusChangeListener { view, b ->
        if (!b && !minutesInput.text.isNullOrEmpty()) {
            when (minutesInput.text.toString().toInt()) {
                in 0..9 -> minutesInput.setText("0" + minutesInput.text.toString())
            }
        }
    }

    secondsInput.setOnFocusChangeListener { view, b ->
        if (!b && !secondsInput.text.isNullOrEmpty()) {
            when (secondsInput.text.toString().toInt()) {
                in 0..9 -> secondsInput.setText("0" + secondsInput.text.toString())
            }
        }
    }

    fun isInputsValid(): Boolean {
        if (includeTimerSwitcher.isChecked) {
            return (!exerciseTitleInput.text.toString().equals(""))
                    && (((!minutesInput.text.toString().equals(""))
                    && (!minutesInput.text.toString().equals("00"))
                    && ((!minutesInput.text.toString().equals("0"))))
                    || ((!secondsInput.text.toString().equals(""))
                    && (!secondsInput.text.toString().equals("00"))
                    && ((!secondsInput.text.toString().equals("0")))))
        } else {
            return (!exerciseTitleInput.text.toString().equals(""))
        }
    }

    includeTimerSwitcher.setOnCheckedChangeListener { compoundButton, isChecked ->
        if (isChecked) {
            durationWrapper.visibility = LinearLayout.VISIBLE
        } else {
            durationWrapper.visibility = LinearLayout.GONE
        }

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
    }

    exerciseTitleInput.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
        }
    })

    minutesInput.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0?.length!! > 1) {
                secondsInput.requestFocus()
            }

            if (p0.toString().isNullOrEmpty()) {
                minutesInput.setText("00")
            }

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
        }
    })

    secondsInput.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.toString().isNullOrEmpty()) {
                minutesInput.requestFocus()
                secondsInput.setText("00")
            }

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isInputsValid()
        }
    })

}