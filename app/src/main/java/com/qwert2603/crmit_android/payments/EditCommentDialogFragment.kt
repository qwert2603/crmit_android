package com.qwert2603.crmit_android.payments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import kotlinx.android.synthetic.main.dialog_edit_payment_comment.view.*

@FragmentWithArgs
class EditCommentDialogFragment : DialogFragment() {

    companion object {
        const val KEY_PAYMENT_ID = "KEY_PAYMENT_ID"
        const val KEY_PAYMENT_COMMENT = "KEY_PAYMENT_COMMENT"
    }

    @Arg
    var paymentId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var comment: String

    private lateinit var dialogView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        @SuppressLint("InflateParams")
        dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_payment_comment, null)
        dialogView.comment_EditText.setText(comment)
        dialogView.comment_EditText.setOnEditorActionListener { _, _, _ ->
            sendResult()
            dismiss()
            true
        }
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_dialog_edit_payment_comment)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok) { _, _ -> sendResult() }
                .create()
                .also {
                    it.setOnShowListener { _ ->
                        dialogView.comment_EditText.requestFocus()
                        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(dialogView.comment_EditText, 0)
                    }
                }
    }

    private fun sendResult() {
        targetFragment!!.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                Intent()
                        .putExtra(KEY_PAYMENT_ID, paymentId)
                        .putExtra(KEY_PAYMENT_COMMENT, dialogView.comment_EditText.text.toString())
        )
    }
}