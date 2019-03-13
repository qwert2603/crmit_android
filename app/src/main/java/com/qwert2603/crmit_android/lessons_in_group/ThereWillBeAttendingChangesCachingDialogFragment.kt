package com.qwert2603.crmit_android.lessons_in_group

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.setFontToTextViews

class ThereWillBeAttendingChangesCachingDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.message_there_will_be_attending_changes_caching)
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
            .setFontToTextViews(this)
}