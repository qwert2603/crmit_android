package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.Bot
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_bot.view.*

class BotsListFragment : EntitiesListFragment<Bot>() {
    override val source = DiHolder.rest::getBotsList
    override val dbDaoInterface: DaoInterface<Bot> = DiHolder.botDaoInterface
    override val titleRes = R.string.title_bots
    override val vhLayoutRes = R.layout.item_bot
    override val entityPluralsRes = R.plurals.bots
    override fun View.bindEntity(e: Bot) {
        fio_TextView.text = e.fio
        disabled_TextView.setVisible(!e.systemUser.enabled)
        fio_TextView.setStrike(!e.systemUser.enabled)
        fio_TextView.transitionName = "entity_name_${e.id}"
        login_TextView.text = e.systemUser.login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.BotDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.fio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUser.enabled,
                            entityNameColorAccent = false
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}