package com.qwert2603.crmit_android.payments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.qwert2603.crmit_android.R

class ThereWillBePaymentChangesCachingDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.message_there_will_be_payment_changes_caching)
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
            .also { it.setCanceledOnTouchOutside(false) }
}