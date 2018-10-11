package com.qwert2603.crmit_android.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.CrmitConst

class UpdateAvailableDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.update_available_title)
            .setMessage(R.string.update_available_message)
            .setPositiveButton(R.string.text_update) { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CrmitConst.LINK_PLAY_MARKET))) }
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
}