package com.qwert2603.crmit_android.lesson_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import kotlinx.android.synthetic.main.item_attending.view.*

class AttendingViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<Attending>(parent, R.layout.item_attending) {
    init {
        itemView.apply {
            attendingStateView.stateChangesFromClicksListener = { state ->
                val attendingId = m?.id
                if (attendingId != null) {
                    (adapter as AttendingsAdapter).attendingStateChanges.onNext(SaveAttendingStateParams(
                            attendingId = attendingId,
                            attendingState = state
                    ))
                }
            }
            uploadError_ImageView.setOnClickListener {
                val attendingId = m?.id
                if (attendingId != null) {
                    (adapter as AttendingsAdapter).attendingStateChanges.onNext(SaveAttendingStateParams(
                            attendingId = attendingId,
                            attendingState = attendingStateView.state
                    ))
                }
            }
        }
    }

    override fun bind(m: Attending) = with(itemView) {
        super.bind(m)
        studentFio_TextView.text = m.studentFio
        attendingStateView.setAttendingState(m.state)
        attendingStateView.userCanClick = (adapter as AttendingsAdapter).userCanChangeAttendingState

        val uploadStatuses = (adapter as AttendingsAdapter).uploadStatuses
        UploadStatus.values().forEachIndexed { index, uploadStatus ->
            uploadStatus_FrameLayout.getChildAt(index).setVisible(uploadStatus == uploadStatuses[m.id])
        }
    }
}