package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.details_fragments.GroupDetailsFragment
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.navigation.ScreenKey
import kotlinx.android.synthetic.main.item_group.view.*

class GroupsListFragment : EntitiesListFragment<GroupBrief>() {
    override val source = DiHolder.rest::getGroupsList

    override val dbDao: DaoInterface<GroupBrief> = DiHolder.groupBriefDao

    override val titleRes = R.string.title_groups

    override val vhLayoutRes = R.layout.item_group

    override val entityPluralsRes = R.plurals.groups

    override fun View.bindEntity(e: GroupBrief) {
        name_TextView.text = e.name
        name_TextView.transitionName = "group_name_${e.id}"
        teacherFio_TextView.text = e.teacherFio
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(ScreenKey.GROUP_DETAILS.name, GroupDetailsFragment.Key(
                            groupId = it.id,
                            groupName = it.name,
                            groupNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.name_TextView
                    ))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}