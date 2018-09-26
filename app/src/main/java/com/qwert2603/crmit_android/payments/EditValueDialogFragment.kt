package com.qwert2603.crmit_android.payments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong

@FragmentWithArgs
class EditValueDialogFragment : DialogFragment() {

    companion object {
        const val KEY_PAYMENT_ID = "KEY_PAYMENT_ID"
        const val KEY_PAYMENT_VALUE = "KEY_PAYMENT_VALUE"
    }

    @Arg
    var paymentId: Long = IdentifiableLong.NO_ID

    @Arg
    var value: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
            .setMessage(value.toString())
            .setPositiveButton(android.R.string.ok) { _, _ ->
                targetFragment!!.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        Intent()
                                .putExtra(KEY_PAYMENT_ID, paymentId)
                                .putExtra(KEY_PAYMENT_VALUE, value + 1)
                )
            }
            .create()
}