package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.db.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import kotlinx.android.synthetic.main.item_group.view.*

class GroupsListFragment : EntitiesListFragment<GroupBrief>() {
    override val source = DiHolder.rest::getGroupsList

    override val dbDaoInterface: DaoInterface<GroupBrief> = DiHolder.groupBriefCustomOrderDao.wrap(DiHolder.userSettingsRepo.loginResult)

    override val titleRes = R.string.title_groups

    override val vhLayoutRes = R.layout.item_group

    override val entityPluralsRes = R.plurals.groups

    override fun View.bindEntity(e: GroupBrief) {
        teacherFio_TextView.setTextColor(resources.color(if (currentViewState.authedUserAccountType == AccountType.TEACHER && e.teacherId == currentViewState.authedUserDetailsId)
            R.color.colorAccent
        else
            android.R.color.black
        ))
        name_TextView.text = e.name
        name_TextView.transitionName = "entity_name_${e.id}"
        teacherFio_TextView.text = e.teacherFio
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.GroupDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.name,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.name_TextView
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}