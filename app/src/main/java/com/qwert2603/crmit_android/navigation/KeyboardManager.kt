package com.qwert2603.crmit_android.navigation

import android.widget.EditText

interface KeyboardManager {
    fun hideKeyboard(removeFocus: Boolean = true)
    fun showKeyboard(editText: EditText)
    fun isKeyBoardShown(): Boolean
}