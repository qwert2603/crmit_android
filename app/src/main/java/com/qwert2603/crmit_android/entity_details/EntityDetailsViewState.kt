package com.qwert2603.crmit_android.entity_details

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger

@GenerateLRChanger
data class EntityDetailsViewState<E : Any>(
        override val lrModel: LRModel,
        val entity: E?
) : LRViewState