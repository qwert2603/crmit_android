package com.qwert2603.crmit_android.payments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Payment
import com.qwert2603.crmit_android.entity.UploadStatus
import com.qwert2603.crmit_android.util.UserInputCompoundButton
import com.qwert2603.crmit_android.util.toPointedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.include_upload_status.view.*
import kotlinx.android.synthetic.main.item_payment.view.*

class PaymentViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<Payment>(parent, R.layout.item_payment), LayoutContainer {

    override val containerView: View = itemView

    private val cashSwitch = UserInputCompoundButton(itemView.cash_Switch)
    private val confirmedSwitch = UserInputCompoundButton(itemView.confirmed_Switch)

    init {
        itemView.apply {
            cashSwitch.userInputListener = { (adapter as PaymentsAdapter).isCashChanges.onNext(m!!.id to it) }
            confirmedSwitch.userInputListener = { (adapter as PaymentsAdapter).isConfirmedChanges.onNext(m!!.id to it) }
            value_TextView.setOnClickListener { (adapter as PaymentsAdapter).askToEditValue.onNext(m!!.id to m!!.value) }
            comment_TextView.setOnClickListener { (adapter as PaymentsAdapter).askToEditComment.onNext(m!!.id to m!!.comment) }
            uploadError_ImageView.setOnClickListener { (adapter as PaymentsAdapter).retryClicks.onNext(m!!.id) }
        }
    }

    override fun bind(m: Payment) = with(itemView) {
        super.bind(m)

        studentFio_TextView.text = m.studentFio
        @SuppressLint("SetTextI18n")
        value_TextView.text = resources.getString(R.string.payment_value_format, m.value.toPointedString(), m.needToPay.toPointedString())
        value_TextView.setTextColor(resources.color(if (m.confirmed) R.color.payment_value_confirmed else android.R.color.black))

        cashSwitch.setChecked(m.cash)
        confirmedSwitch.setChecked(m.confirmed)
        comment_TextView.text = m.comment.takeIf { it.isNotBlank() } ?: resources.getString(R.string.text_no_comment)
        comment_TextView.setTypeface(null, if (m.comment.isNotBlank()) Typeface.NORMAL else Typeface.ITALIC)

        listOf(value_TextView, cash_Switch, comment_TextView)
                .forEach { it.isEnabled = !m.confirmed }

        confirmed_Switch.isEnabled = (adapter as PaymentsAdapter).userCanConfirm

        val uploadStatuses = (adapter as PaymentsAdapter).uploadStatuses
        UploadStatus.values().forEachIndexed { index, uploadStatus ->
            uploadStatus_FrameLayout.getChildAt(index).setVisible(uploadStatus == uploadStatuses[m.id])
        }
    }
}