package com.qwert2603.crmit_android.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import com.qwert2603.crmit_android.R
import kotlinx.android.synthetic.main.dialog_update_available.view.*

class UpdateAvailableDialog : DialogFragment() {

    @SuppressLint("InflateParams")
    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_available, null)
        view.message_TextView.text = Html.fromHtml(getString(R.string.update_available_message))
        view.message_TextView.movementMethod = LinkMovementMethod.getInstance()
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.update_available_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .also { it.setCanceledOnTouchOutside(false) }
    }
}