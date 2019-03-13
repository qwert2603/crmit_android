package com.qwert2603.crmit_android.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import com.qwert2603.crmit_android.BuildConfig
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.util.setFontToTextViews

class WhatsNewDialog : DialogFragment() {

    companion object {
        fun showIfNeeded(fragmentManager: FragmentManager): Boolean {
            if (BuildConfig.VERSION_CODE > DiHolder.userSettingsRepo.whatsNewVersionCodeShown) {
                DiHolder.userSettingsRepo.whatsNewVersionCodeShown = BuildConfig.VERSION_CODE
                WhatsNewDialog().show(fragmentManager, null)
                return true
            }
            return false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.whats_new_title, BuildConfig.VERSION_NAME))
            .setMessage(R.string.whats_new_message)
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
            .setFontToTextViews(this)
}