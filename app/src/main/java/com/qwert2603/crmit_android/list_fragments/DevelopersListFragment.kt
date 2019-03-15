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
import com.qwert2603.crmit_android.entity.Developer
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_developer.view.*

class DevelopersListFragment : EntitiesListFragment<Developer>() {
    override val source = DiHolder.rest::getDevelopersList
    override val dbDaoInterface: DaoInterface<Developer> = DiHolder.developerDaoInterface
    override val titleRes = R.string.title_developers
    override val vhLayoutRes = R.layout.item_developer
    override val entityPluralsRes = R.plurals.developers
    override fun View.bindEntity(e: Developer) {
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
                    DiHolder.router.navigateTo(Screen.DeveloperDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.fio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUser.enabled,
                            entityNameColorAccent = it.isAuthed()
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun Developer.isAuthed() = currentViewState.authedUserAccountType == AccountType.DEVELOPER && this.id == currentViewState.authedUserDetailsId
}