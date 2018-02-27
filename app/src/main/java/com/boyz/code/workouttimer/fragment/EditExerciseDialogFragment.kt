package com.boyz.code.workouttimer.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import com.boyz.code.workouttimer.R
import com.boyz.code.workouttimer.data.Exercise
import com.boyz.code.workouttimer.misc.setExerciseDialogValidators
import com.boyz.code.workouttimer.misc.timeInputConverter
import kotlinx.android.synthetic.main.dialog_edit_exercise.view.*

class EditExerciseDialogFragment : DialogFragment() {

    var onConfirmedListener: ((Exercise) -> Unit)? = null
    var onDeleteListener: (() -> Unit)? = null

    companion object {
        val ARG_TITLE = "default"
        val ARG_LENGTH = "length"

        fun create(exerciseTitle: String, exerciseLength: String) : EditExerciseDialogFragment {
            return EditExerciseDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, exerciseTitle)
                    putString(ARG_LENGTH, exerciseLength)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_edit_exercise, null)
        val times = arguments.getString(ARG_LENGTH).split(":")

        val exerciseTitleEditInput = view.exerciseTitleEditInput
        val minutesEditInput = view.minutesEditInput
        val secondsEditInput = view.secondsEditInput
        val includeTimerEditSwitcher = view.includeTimerEditSwitcher
        val durationEditWrapper = view.durationEditWrapper
        val deleteExerciseBtn = view.deleteExerciseBtn

        exerciseTitleEditInput.setText(arguments.getString(ARG_TITLE))
        minutesEditInput.setText(times[0])
        secondsEditInput.setText(times[1])

        deleteExerciseBtn.setOnClickListener {
            onDeleteListener!!()
        }

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle("Edit exercise")
                .setView(view)
                .setPositiveButton("OK") {
                    dialog, which ->
                    val length = when (includeTimerEditSwitcher.isChecked) {
                        false -> 0
                        else -> timeInputConverter(minutesEditInput.text.toString().toInt(), secondsEditInput.text.toString().toInt())
                    }

                    val exercise = Exercise(exerciseTitleEditInput.text.toString(), length)

                    if (onConfirmedListener != null) {
                        onConfirmedListener!!(exercise)
                    }
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

        if (arguments.getString(ARG_LENGTH) == "00:00")
            includeTimerEditSwitcher.isChecked = false

        return alertDialog
    }
}