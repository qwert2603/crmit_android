package com.qwert2603.crmit_android.student_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.crmit_android.R

class StudentDetailsAdapter : BaseRecyclerViewAdapter<StudentDetailsListItem>() {

    override fun getItemViewTypeModel(m: StudentDetailsListItem) = when (m) {
        is StudentDetailsField -> R.layout.item_student_details_field
        is StudentDetailsSystemInfo -> R.layout.item_student_details_system_info
        is StudentDetailsGroupsList -> R.layout.item_student_details_groups_list
    }

    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_student_details_field -> StudentDetailsViewHolder(parent)
        R.layout.item_student_details_system_info -> StudentDetailsSystemInfoViewHolder(parent)
        R.layout.item_student_details_groups_list -> StudentDetailsGroupsListViewHolder(parent)
        else -> null!!
    } as BaseRecyclerViewHolder<StudentDetailsListItem>
}