package com.qwert2603.crmit_android.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.CrmitConst
import com.qwert2603.crmit_android.util.setFontToTextViews

class UpdateAvailableDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.update_available_title)
            .setMessage(R.string.update_available_message)
            .setPositiveButton(R.string.text_update) { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CrmitConst.LINK_PLAY_MARKET))) }
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
            .setFontToTextViews(this)
}