package com.qwert2603.crmit_android.payments

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Payment
import com.qwert2603.crmit_android.lesson_details.UploadStatus
import io.reactivex.subjects.PublishSubject

class PaymentsAdapter : BaseRecyclerViewAdapter<Payment>() {

    val isCashChanges = PublishSubject.create<Pair<Long, Boolean>>()
    val isConfirmedChanges = PublishSubject.create<Pair<Long, Boolean>>()
    val askToEditValue = PublishSubject.create<Pair<Long, Int>>()
    val askToEditComment = PublishSubject.create<Pair<Long, String>>()
    val retryClicks = PublishSubject.create<Long>()

    var uploadStatuses: Map<Long, UploadStatus> = emptyMap()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    var userCanConfirm = false

    override fun pluralsRes() = R.plurals.students

    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = PaymentViewHolder(parent)
}