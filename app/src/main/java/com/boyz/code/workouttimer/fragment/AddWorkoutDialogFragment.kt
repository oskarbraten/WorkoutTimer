package com.boyz.code.workouttimer.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

import com.boyz.code.workouttimer.R
import kotlinx.android.synthetic.main.dialog_add_workout.*
import android.text.Editable
import android.text.TextWatcher
import com.boyz.code.workouttimer.misc.WorkoutManager
import kotlinx.android.synthetic.main.dialog_add_workout.view.*


class AddWorkoutDialogFragment : DialogFragment() {

    var onConfirmedListener: ((String, String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity.layoutInflater.inflate(R.layout.dialog_add_workout, null, false)

        val dialog = AlertDialog.Builder(activity)
                .setTitle("Add workout")
                .setView(view)
                .setPositiveButton("Add workout") { dialog, which ->
                    if (onConfirmedListener != null) {
                        onConfirmedListener!!(view.addWorkoutDialogNameInput.text.toString(), view.addWorkoutDialogDescriptionInput.text.toString())
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

            // enable positive button when the name input is not empty
            dialog.addWorkoutDialogNameInput.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = s.isNotEmpty() && !WorkoutManager.doesWorkoutExist(activity, s.toString())
                }
            })
        }

        return dialog
    }

    override fun onDetach() {
        super.onDetach()
        onConfirmedListener = null
    }
}
