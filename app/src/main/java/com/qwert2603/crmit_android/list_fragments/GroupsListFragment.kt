package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.GroupBrief
import kotlinx.android.synthetic.main.item_group.view.*

class GroupsListFragment : EntitiesListFragment<GroupBrief>() {
    override val source = DiHolder.rest::getGroupsList

    override val dbDao: DaoInterface<GroupBrief> = DiHolder.groupBriefDao

    override val titleRes = R.string.title_groups

    override val vhLayoutRes = R.layout.item_group

    override val entityPluralsRes = R.plurals.groups

    override fun View.bindEntity(e: GroupBrief) {
        name_TextView.text = e.name
        teacherFio_TextView.text = e.teacherFio
    }
}