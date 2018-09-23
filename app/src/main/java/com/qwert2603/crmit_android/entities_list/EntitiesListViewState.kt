package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListModel
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.andrlib.generator.GenerateListChanger
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.entity.AccountType

@GenerateLRChanger
@GenerateListChanger
data class EntitiesListViewState<E : IdentifiableLong>(
        override val lrModel: LRModel,
        override val listModel: ListModel,
        override val showingList: List<E>,
        val searchOpen: Boolean,
        val searchQuery: String,
        val authedUserAccountType: AccountType?,
        val authedUserDetailsId: Long?
) : ListViewState<E>