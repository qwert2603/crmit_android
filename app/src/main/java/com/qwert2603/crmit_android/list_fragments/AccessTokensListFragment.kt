package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.EmptyDaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccessTokenItem
import com.qwert2603.crmit_android.entity.SystemUser
import com.qwert2603.crmit_android.util.toLastSeenString
import kotlinx.android.synthetic.main.item_access_token.view.*

class AccessTokensListFragment : EntitiesListFragment<AccessTokenItem>() {

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getAccessTokens() }

    override val dbDaoInterface = EmptyDaoInterface<AccessTokenItem>()

    override val titleRes = R.string.title_access_token

    override val vhLayoutRes = R.layout.item_access_token

    override val pageSize = Int.MAX_VALUE

    override fun View.bindEntity(e: AccessTokenItem) {
        fio_TextView.text = e.fio
        login_TextView.text = e.systemUser.login
        roleName_TextView.text = e.systemUser.systemRoleName
        // dirty, but works.
        expires_TextView.text = SystemUser(
                id = IdentifiableLong.NO_ID,
                login = "",
                lastSeen = e.expires,
                lastSeenWhere = SystemUser.LAST_SEEN_WEB,
                systemRoleName = "",
                enabled = false
        ).toLastSeenString(resources)
    }

    override var withSearch = false
}