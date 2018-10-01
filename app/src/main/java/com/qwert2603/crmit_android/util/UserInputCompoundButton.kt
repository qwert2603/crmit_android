package com.qwert2603.crmit_android.util

import android.widget.CompoundButton

class UserInputCompoundButton(private val compoundButton: CompoundButton) {
    private var userInput = true

    var userInputListener: ((Boolean) -> Unit)? = null

    init {
        compoundButton.isSaveEnabled = false

        compoundButton.setOnCheckedChangeListener { _, isChecked ->
            if (userInput) userInputListener?.invoke(isChecked)
        }
    }

    fun setChecked(checked: Boolean) {
        userInput = false
        if (compoundButton.isChecked != checked) {
            compoundButton.isChecked = checked
        }
        userInput = true
    }
}