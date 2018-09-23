package com.qwert2603.crmit_android.entity_details

import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_entity_details_field.view.*

class EntityDetailsViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<EntityDetailsField>(parent, R.layout.item_entity_details_field), LayoutContainer {
    override val containerView: View = itemView

    private val clickableBackground = itemView.background

    override fun bind(m: EntityDetailsField) = with(itemView) {
        super.bind(m)

        if (m.clickCallback != null) {
            setOnClickListener { (m.clickCallback)() }
            background = clickableBackground
        } else {
            setOnClickListener(null)
            background = null
        }

        fieldValue_TextView.setTextColor(resources.color(m.textColorRes ?: android.R.color.black))
        fieldName_TextView.setText(m.fieldTitleStringRes)
        fieldValue_TextView.text = m.fieldValue
        fieldValue_TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                m.iconDrawableRes ?: 0,
                0,
                if (m.clickCallback != null) R.drawable.ic_navigate_next_black_24dp else 0,
                0
        )
    }
}