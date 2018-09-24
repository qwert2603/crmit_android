package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Master
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsSystemInfo
import com.qwert2603.crmit_android.entity_details.EntityDetailsViewState
import com.qwert2603.crmit_android.util.toLastSeenString

@FragmentWithArgs
class MasterDetailsFragment : EntityDetailsFragment<Master>() {

    override val source = DiHolder.rest::getMasterDetails

    override val dbDaoInterface = DiHolder.masterDaoInterface

    override fun Master.entityName() = fio

    override fun Master.entityNameStrike() = !systemUser.enabled

    override fun EntityDetailsViewState<Master>.entityNameColorAccent() = authedUserAccountType == AccountType.MASTER && entity != null && entity.id == authedUserDetailsId

    override fun Master.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_systemRoleName, systemUser.systemRoleName),
            EntityDetailsField(R.string.detailsField_lastSeen, systemUser.toLastSeenString(resources))
    )
}