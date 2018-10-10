package com.qwert2603.crmit_android.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import kotlinx.android.synthetic.main.dialog_mark_in_play_marker.view.*

class MarkInPlayMarketDialog : DialogFragment() {

    companion object {
        fun showIfNeeded(fragmentManager: FragmentManager) {
            if (DiHolder.userSettingsRepo.launchesCount == 8) {
                MarkInPlayMarketDialog().show(fragmentManager, null)
            }
        }
    }

    @SuppressLint("InflateParams")
    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mark_in_play_marker, null)
        view.message_TextView.text = Html.fromHtml(getString(R.string.mark_in_play_marker_message))
        view.message_TextView.movementMethod = LinkMovementMethod.getInstance()
        return AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .also { it.setCanceledOnTouchOutside(false) }
    }
}