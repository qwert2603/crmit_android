package com.qwert2603.crmit_android.cabinet

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.crmit_android.entity.Lesson

class LastLessonAdapter : BaseRecyclerViewAdapter<Lesson>() {
    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = LastLessonViewHolder(parent)
}