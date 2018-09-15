package com.qwert2603.crmit_android.entity_details

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.GroupBrief
import kotlinx.android.synthetic.main.item_entity_details_group.view.*
import kotlinx.android.synthetic.main.item_entity_details_groups_list.view.*

class EntityDetailsGroupsListViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<EntityDetailsGroupsList>(parent, R.layout.item_entity_details_groups_list) {

    private class GroupVH(parent: ViewGroup) : BaseRecyclerViewHolder<GroupBrief>(parent, R.layout.item_entity_details_group) {
        override fun bind(m: GroupBrief) = with(itemView) {
            super.bind(m)
            @SuppressLint("SetTextI18n")
            groupName_TextView.text = "${m.name} (${m.teacherFio})"
        }
    }

    private class GroupsAdapter : BaseRecyclerViewAdapter<GroupBrief>() {
        override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = GroupVH(parent)
    }

    private val groupsAdapter = GroupsAdapter()

    init {
        itemView.groups_RecyclerView.itemAnimator = null
        itemView.groups_RecyclerView.adapter = groupsAdapter
    }

    override fun bind(m: EntityDetailsGroupsList) = with(itemView) {
        super.bind(m)
        groups_RecyclerView.setVisible(m.groups.isNotEmpty())
        noGroups_TextView.setVisible(m.groups.isEmpty())
        groupsAdapter.adapterList = BaseRecyclerViewAdapter.AdapterList(m.groups)
    }
}