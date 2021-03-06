package com.qwert2603.crmit_android.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.util.CrmitConst
import com.qwert2603.crmit_android.util.setFontToTextViews

class MarkInPlayMarketDialog : DialogFragment() {

    companion object {
        fun showIfNeeded(fragmentManager: FragmentManager): Boolean {
            DiHolder.userSettingsRepo.launchesCount++
            if (DiHolder.userSettingsRepo.launchesCount == 4) {
                MarkInPlayMarketDialog().show(fragmentManager, null)
                return true
            }
            return false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.mark_in_play_marker_message)
            .setPositiveButton(R.string.text_mark) { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CrmitConst.LINK_PLAY_MARKET))) }
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
            .setFontToTextViews(this)
}