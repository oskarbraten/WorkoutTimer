package com.boyz.code.workouttimer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.misc.setExerciseDialogValidators
import kotlinx.android.synthetic.main.edit_exercise_dialog.view.*

class EditExerciseFragment : DialogFragment() {

    companion object {
        val TAG = EditExerciseFragment::class.qualifiedName
        val ARG_TITLE = "default"
        val ARG_LENGTH = "00:00"

        fun show(activity: Activity, exerciseTitle: String, exerciseLength: String) {
            EditExerciseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, exerciseTitle)
                    putString(ARG_LENGTH, exerciseLength)
                }
            }.show(activity.fragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.edit_exercise_dialog, null)
        val times = arguments.getString(ARG_LENGTH).split(":")

        val exerciseTitleEditInput = view.exerciseTitleEditInput
        val minutesEditInput = view.minutesEditInput
        val secondsEditInput = view.secondsEditInput
        val includeTimerEditSwitcher = view.includeTimerEditSwitcher
        val durationEditWrapper = view.durationEditWrapper

        exerciseTitleEditInput.setText(arguments.getString(ARG_TITLE))
        minutesEditInput.setText(times[0])
        secondsEditInput.setText(times[1])

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle("Edit")
                .setView(view)
                .setPositiveButton("OK") {
                    dialog, which ->
                    Log.d("hmm", "jaja")
                }
                .setNegativeButton("Cancel") {
                    dialog, which ->
                    Log.d("hmm", "jaja")
                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }

        setExerciseDialogValidators(exerciseTitleEditInput, includeTimerEditSwitcher, minutesEditInput, secondsEditInput, durationEditWrapper, alertDialog)

        return alertDialog
    }
}