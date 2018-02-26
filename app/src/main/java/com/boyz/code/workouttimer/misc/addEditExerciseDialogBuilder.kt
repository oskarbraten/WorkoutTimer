package com.boyz.code.workouttimer.misc

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch

fun buildAddEditDialog(exerciseTitleInput: EditText, includeTimerSwitcher: Switch, minutesInput: EditText, secondsInput: EditText, durationWrapper: LinearLayout, alertDialog: AlertDialog){

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
