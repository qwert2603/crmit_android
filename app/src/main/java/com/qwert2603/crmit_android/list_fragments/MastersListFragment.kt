package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Master
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.ScreenKey
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_master.view.*

class MastersListFragment : EntitiesListFragment<Master>() {
    override val source = DiHolder.rest::getMastersList
    override val dbDaoInterface: DaoInterface<Master> = DiHolder.masterDaoInterface
    override val titleRes = R.string.title_masters
    override val vhLayoutRes = R.layout.item_master
    override val entityPluralsRes = R.plurals.masters
    override fun View.bindEntity(e: Master) {
        fio_TextView.setTextColor(resources.color(if (e.isAuthed()) R.color.colorAccent else android.R.color.black))
        fio_TextView.text = e.fio
        disabled_TextView.setVisible(!e.systemUser.enabled)
        fio_TextView.setStrike(!e.systemUser.enabled)
        fio_TextView.transitionName = "entity_name_${e.id}"
        login_TextView.text = e.systemUser.login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(ScreenKey.MASTER_DETAILS.name, EntityDetailsFragment.Key(
                            entityId = it.id,
                            entityName = it.fio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUser.enabled,
                            entityNameColorAccent = it.isAuthed()
                    ))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun Master.isAuthed() = currentViewState.authedUserAccountType == AccountType.MASTER && this.id == currentViewState.authedUserDetailsId
}