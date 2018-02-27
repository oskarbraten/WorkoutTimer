package com.boyz.code.workouttimer.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.misc.setExerciseDialogValidators
import com.boyz.code.workouttimer.misc.timeInputConverter
import kotlinx.android.synthetic.main.dialog_add_exercise.view.*

class AddExerciseDialogFragment : DialogFragment() {

    var onConfirmedListener: ((Exercise) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_exercise, null)

        val exerciseTitleInput = view.exerciseTitleInput
        val minutesInput = view.minutesInput
        val secondsInput = view.secondsInput
        val includeTimerSwitcher = view.includeTimerSwitcher
        val durationWrapper = view.durationWrapper

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle("Add exercise")
                .setView(view)
                .setPositiveButton("OK") {
                    dialog, which ->
                    val length = when (includeTimerSwitcher.isChecked) {
                        false -> 0
                        else -> timeInputConverter(minutesInput.text.toString().toInt(), secondsInput.text.toString().toInt())
                    }

                    val exercise = Exercise(exerciseTitleInput.text.toString(), length)

                    if (onConfirmedListener != null) {
                        onConfirmedListener!!(exercise)
                    }
                }
                .setNegativeButton("Cancel") {
                    dialog, which ->

                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }

        setExerciseDialogValidators(exerciseTitleInput, includeTimerSwitcher, minutesInput, secondsInput, durationWrapper, alertDialog)
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        val dialog: AlertDialog = dialog as AlertDialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }
}