package com.qwert2603.crmit_android.lesson_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.UploadStatus
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import io.reactivex.subjects.PublishSubject

class AttendingsAdapter : BaseRecyclerViewAdapter<Attending>() {

    init {
        // App crashed w/o this, because Attending can't be cast to AllItemsLoaded.
        // And i don't now why it happens.
        // AdapterList is created correctly and it returns correct items via AdapterList#get(position) method.
        // May be new AdapterList is setted to adapter while calculating diffs for prev list.
        // =\
        useDiffUtils = false
    }

    val attendingStateChanges = PublishSubject.create<SaveAttendingStateParams>()

    var uploadStatuses: Map<Long, UploadStatus> = emptyMap()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    var userCanChangeAttendingState = false

    override fun pluralsRes() = R.plurals.students

    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = AttendingViewHolder(parent)
}