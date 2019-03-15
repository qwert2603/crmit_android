package com.qwert2603.crmit_android.list_fragments

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.EmptyDaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccessTokensItem
import com.qwert2603.crmit_android.entity.SystemUser
import com.qwert2603.crmit_android.util.toLastSeenString
import kotlinx.android.synthetic.main.item_access_token.view.*

class AccessTokensListFragment : EntitiesListFragment<AccessTokensItem>() {

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getAccessTokens() }

    override val dbDaoInterface = EmptyDaoInterface<AccessTokensItem>()

    override val titleRes = R.string.title_access_token

    override val vhLayoutRes = R.layout.item_access_token

    override val pageSize = Int.MAX_VALUE

    override fun View.bindEntity(e: AccessTokensItem) {
        fio_TextView.text = e.fio
        login_TextView.text = e.systemUser.login
        roleName_TextView.text = e.systemUser.systemRoleName
        expiresList_TextView.text = e.tokens.listToString()
    }

    override var withSearch = false

    private fun List<AccessTokensItem.Token>.listToString(): SpannableStringBuilder = this
            .map { token ->
                // dirty, but works.
                val lastUseString = SystemUser(
                        id = IdentifiableLong.NO_ID,
                        login = "",
                        lastSeen = token.lastUse,
                        lastSeenWhere = SystemUser.LAST_SEEN_WEB,
                        systemRoleName = "",
                        enabled = false
                ).toLastSeenString(resources)

                val s = "$lastUseString\n${token.appVersion} / ${token.device}"
                val spannableString = SpannableStringBuilder(s)
                val foregroundAccentSpan = ForegroundColorSpan(resources.color(R.color.colorAccent))
                spannableString.setSpan(foregroundAccentSpan, 0, lastUseString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val foregroundGreySpan = ForegroundColorSpan(resources.color(R.color.gray_text))
                spannableString.setSpan(foregroundGreySpan, lastUseString.length + 1, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString
            }
            .reduce { acc, s -> acc.append("\n\n").append(s) }
}