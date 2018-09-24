package com.qwert2603.crmit_android.lesson_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import io.reactivex.subjects.PublishSubject

class AttendingsAdapter : BaseRecyclerViewAdapter<Attending>() {

    val attendingStateChanges = PublishSubject.create<SaveAttendingStateParams>()

    var uploadStatuses: Map<Long, UploadStatus> = emptyMap()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = AttendingViewHolder(parent)
}