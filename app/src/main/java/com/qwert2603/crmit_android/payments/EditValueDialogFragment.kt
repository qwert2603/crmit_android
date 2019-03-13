package com.qwert2603.crmit_android.payments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.doAfterTextChanged
import com.qwert2603.crmit_android.util.toPointedString
import kotlinx.android.synthetic.main.dialog_edit_payment_value.view.*

@FragmentWithArgs
class EditValueDialogFragment : DialogFragment() {

    companion object {
        const val KEY_PAYMENT_ID = "KEY_PAYMENT_ID"
        const val KEY_PAYMENT_VALUE = "KEY_PAYMENT_VALUE"
    }

    @Arg
    var paymentId: Long = IdentifiableLong.NO_ID

    @Arg
    var value: Int = 0

    @Arg
    var maxValue: Int = 0

    @Arg
    lateinit var title: String

    private lateinit var dialogView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        @SuppressLint("InflateParams")
        dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_payment_value, null)
        dialogView.maxValue_TextView.text = resources.getString(R.string.max_payment_value_format, maxValue.toPointedString())
        if (value > 0) {
            dialogView.value_EditText.setText(value.toString())
        }
        dialogView.value_EditText.setOnEditorActionListener { _, _, _ ->
            sendResult()
            dismiss()
            true
        }
        dialogView.value_EditText.doAfterTextChanged {
            val newValue = it.toIntOrNull()
            if (newValue != null && newValue > maxValue) {
                dialogView.value_EditText.setText(maxValue.toString())
                dialogView.value_EditText.setSelection(maxValue.toString().length)
            }
        }
        return AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok) { _, _ -> sendResult() }
                .create()
                .also {
                    it.setOnShowListener { _ ->
                        dialogView.value_EditText.requestFocus()
                        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(dialogView.value_EditText, 0)
                    }
                }
    }

    private fun sendResult() {
        val value = dialogView.value_EditText.text.toString().toIntOrNull() ?: 0
        targetFragment!!.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                Intent()
                        .putExtra(KEY_PAYMENT_ID, paymentId)
                        .putExtra(KEY_PAYMENT_VALUE, value)
        )
    }
}