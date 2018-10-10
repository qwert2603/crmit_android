package com.qwert2603.crmit_android.entity_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import kotlinx.android.synthetic.main.item_entity_details_group.view.*
import kotlinx.android.synthetic.main.item_entity_details_groups_list.view.*

class EntityDetailsGroupsListViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<EntityDetailsGroupsList>(parent, R.layout.item_entity_details_groups_list) {

    private inner class GroupVH(parent: ViewGroup) : BaseRecyclerViewHolder<GroupBrief>(parent, R.layout.item_entity_details_group) {
        override fun bind(m: GroupBrief) = with(itemView) {
            super.bind(m)
            val showTeacherFio = this@EntityDetailsGroupsListViewHolder.m!!.showTeacherFio
            groupName_TextView.text = if (showTeacherFio) "${m.name} (${m.teacherFio})" else m.name

            val entityDetailsAdapter = this@EntityDetailsGroupsListViewHolder.adapter as EntityDetailsAdapter
            groupName_TextView.setTextColor(resources.color(
                    if (entityDetailsAdapter.authedUserAccountType == AccountType.TEACHER && entityDetailsAdapter.authedUserDetailsId == m.teacherId)
                        R.color.colorAccent
                    else
                        android.R.color.black
            ))
        }
    }

    private inner class GroupsAdapter : BaseRecyclerViewAdapter<GroupBrief>() {
        override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = GroupVH(parent)
    }

    private val groupsAdapter = GroupsAdapter()

    init {
        itemView.groups_RecyclerView.itemAnimator = null
        itemView.groups_RecyclerView.adapter = groupsAdapter
        groupsAdapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.GroupDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.name
                    )))
                }
    }

    override fun bind(m: EntityDetailsGroupsList) = with(itemView) {
        super.bind(m)
        groups_RecyclerView.setVisible(m.groups.isNotEmpty())
        noGroups_TextView.setVisible(m.groups.isEmpty())
        groupsAdapter.adapterList = BaseRecyclerViewAdapter.AdapterList(m.groups)
    }
}