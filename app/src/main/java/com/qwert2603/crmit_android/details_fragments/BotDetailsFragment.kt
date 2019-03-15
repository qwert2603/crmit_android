package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Bot
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsSystemInfo
import com.qwert2603.crmit_android.entity_details.EntityDetailsViewState
import com.qwert2603.crmit_android.util.toLastSeenString

@FragmentWithArgs
class BotDetailsFragment : EntityDetailsFragment<Bot>() {

    override val source = DiHolder.rest::getBotDetails

    override val dbDaoInterface = DiHolder.botDaoInterface

    override fun Bot.entityName() = fio

    override fun Bot.entityNameStrike() = !systemUser.enabled

    override fun EntityDetailsViewState<Bot>.entityNameColorAccent() = false

    override fun Bot.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_systemRoleName, systemUser.systemRoleName),
            EntityDetailsField(R.string.detailsField_lastSeen, systemUser.toLastSeenString(resources))
    )
}