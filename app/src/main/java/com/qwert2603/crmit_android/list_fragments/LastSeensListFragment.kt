package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.EmptyDaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.LastSeenItem
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.toLastSeenString
import kotlinx.android.synthetic.main.item_last_seen.view.*

class LastSeensListFragment : EntitiesListFragment<LastSeenItem>() {

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getLastSeens() }

    override val dbDaoInterface = EmptyDaoInterface<LastSeenItem>()

    override val titleRes = R.string.title_last_seens

    override val vhLayoutRes = R.layout.item_last_seen

    override val pageSize = Int.MAX_VALUE

    override fun View.bindEntity(e: LastSeenItem) {
        fio_TextView.text = e.fio
        login_TextView.text = e.systemUser.login
        roleName_TextView.text = e.systemUser.systemRoleName
        lastSeen_TextView.text = e.systemUser.toLastSeenString(resources)
    }

    override var withSearch = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    val detailsScreenKey = DetailsScreenKey(
                            entityId = it.detailsId,
                            entityName = it.fio
                    )
                    DiHolder.router.navigateTo(when (it.systemUser.accountType) {
                        AccountType.MASTER -> Screen.MasterDetails(detailsScreenKey)
                        AccountType.TEACHER -> Screen.TeacherDetails(detailsScreenKey)
                        AccountType.DEVELOPER -> Screen.DeveloperDetails(detailsScreenKey)
                        AccountType.BOT -> Screen.BotDetails(detailsScreenKey)
                        AccountType.STUDENT -> Screen.StudentDetails(detailsScreenKey)
                    })
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}