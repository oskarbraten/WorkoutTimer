package com.boyz.code.workouttimer.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.misc.setExerciseDialogValidators
import kotlinx.android.synthetic.main.dialog_add_exercise.view.*

class AddExerciseDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_exercise, null)

        val exerciseTitleEditInput = view.exerciseTitleInput
        val minutesEditInput = view.minutesInput
        val secondsEditInput = view.secondsInput
        val includeTimerEditSwitcher = view.includeTimerSwitcher
        val durationEditWrapper = view.durationWrapper

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle("Add exercise")
                .setView(view)
                .setPositiveButton("OK") {
                    dialog, which ->

                }
                .setNegativeButton("Cancel") {
                    dialog, which ->

                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }
                .apply {
                    getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                }

        setExerciseDialogValidators(exerciseTitleEditInput, includeTimerEditSwitcher, minutesEditInput, secondsEditInput, durationEditWrapper, alertDialog)

        return alertDialog
    }
}