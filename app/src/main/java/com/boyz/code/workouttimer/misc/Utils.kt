package com.boyz.code.workouttimer.misc

/**
 * Created by daniel on 24.02.18.
 */

public fun Int?.convertLength(): String {

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