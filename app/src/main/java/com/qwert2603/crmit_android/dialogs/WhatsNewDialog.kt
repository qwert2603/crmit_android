package com.qwert2603.crmit_android.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import com.qwert2603.crmit_android.BuildConfig
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder

class WhatsNewDialog : DialogFragment() {

    companion object {
        fun showIfNeeded(fragmentManager: FragmentManager) {
            if (BuildConfig.VERSION_CODE > DiHolder.userSettingsRepo.whatsNewVersionCodeShown) {
                DiHolder.userSettingsRepo.whatsNewVersionCodeShown = BuildConfig.VERSION_CODE
                WhatsNewDialog().show(fragmentManager, null)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.whats_new_title, BuildConfig.VERSION_NAME))
            .setMessage(R.string.whats_new_message)
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
}