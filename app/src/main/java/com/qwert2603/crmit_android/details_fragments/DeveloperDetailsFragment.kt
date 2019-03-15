package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Developer
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsSystemInfo
import com.qwert2603.crmit_android.entity_details.EntityDetailsViewState
import com.qwert2603.crmit_android.util.toLastSeenString

@FragmentWithArgs
class DeveloperDetailsFragment : EntityDetailsFragment<Developer>() {

    override val source = DiHolder.rest::getDeveloperDetails

    override val dbDaoInterface = DiHolder.developerDaoInterface

    override fun Developer.entityName() = fio

    override fun Developer.entityNameStrike() = !systemUser.enabled

    override fun EntityDetailsViewState<Developer>.entityNameColorAccent() = authedUserAccountType == AccountType.DEVELOPER && entity != null && entity.id == authedUserDetailsId

    override fun Developer.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_systemRoleName, systemUser.systemRoleName),
            EntityDetailsField(R.string.detailsField_lastSeen, systemUser.toLastSeenString(resources))
    )
}