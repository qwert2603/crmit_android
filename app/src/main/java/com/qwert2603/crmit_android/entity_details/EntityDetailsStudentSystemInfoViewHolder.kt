package com.qwert2603.crmit_android.entity_details

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import kotlinx.android.synthetic.main.item_entity_details_student_system_info.view.*

class EntityDetailsStudentSystemInfoViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<EntityDetailsStudentSystemInfo>(parent, R.layout.item_entity_details_student_system_info) {
    override fun bind(m: EntityDetailsStudentSystemInfo) = with(itemView) {
        super.bind(m)
        systemInfo_LinearLayout.setVisible(!m.enabled || !m.filled)
        disabled_TextView.setVisible(!m.enabled)
        notFilled_TextView.setVisible(!m.filled)
    }
}