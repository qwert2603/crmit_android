package com.qwert2603.crmit_android.entity_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.crmit_android.R

class EntityDetailsAdapter : BaseRecyclerViewAdapter<EntityDetailsListItem>() {

    override fun getItemViewTypeModel(m: EntityDetailsListItem) = when (m) {
        is EntityDetailsField -> R.layout.item_entity_details_field
        is EntityDetailsStudentSystemInfo -> R.layout.item_entity_details_student_system_info
        is EntityDetailsGroupsList -> R.layout.item_entity_details_groups_list
    }

    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_entity_details_field -> EntityDetailsViewHolder(parent)
        R.layout.item_entity_details_student_system_info -> EntityDetailsStudentSystemInfoViewHolder(parent)
        R.layout.item_entity_details_groups_list -> EntityDetailsGroupsListViewHolder(parent)
        else -> null!!
    } as BaseRecyclerViewHolder<EntityDetailsListItem>
}