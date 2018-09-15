package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsListItem
import io.reactivex.Single

@FragmentWithArgs
class GroupDetailsFragment:EntityDetailsFragment<GroupFull>() {
    override val entityId: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val source: (entityId: Long) -> Single<GroupFull>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val dbDao: DaoInterface<GroupFull>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun GroupFull.toDetailsList(): List<EntityDetailsListItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}