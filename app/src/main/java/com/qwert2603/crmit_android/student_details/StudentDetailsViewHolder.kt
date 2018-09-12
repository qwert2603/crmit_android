package com.qwert2603.crmit_android.student_details

import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.crmit_android.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_student_details_field.view.*

class StudentDetailsViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<StudentDetailsField>(parent, R.layout.item_student_details_field), LayoutContainer {
    override val containerView: View = itemView

    override fun bind(m: StudentDetailsField) = with(itemView) {
        super.bind(m)
        fieldName_TextView.setText(m.fieldTitleStringRes)
        fieldValue_TextView.text = m.fieldValue
        fieldValue_TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                m.iconDrawableRes ?: 0,
                0,
                0,
                0
        )
    }
}