package com.qwert2603.crmit_android.navigation

import android.graphics.PorterDuff
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import kotlinx.android.synthetic.main.item_navigation_menu.view.*

class NavigationItemViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<NavigationItem>(parent, R.layout.item_navigation_menu) {

    override fun bind(m: NavigationItem) = with(itemView) {
        super.bind(m)
        icon_ImageView.setImageResource(m.iconRes)
        title_TextView.setText(m.titleRes)
        val selected = itemId == (adapter as? NavigationAdapter)?.selectedItemId
        itemView.isSelected = selected
        with(itemView) {
            val tintColor = resources.color(if (selected) R.color.colorAccent else android.R.color.black)
            title_TextView.setTextColor(tintColor)
            icon_ImageView.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
        }
    }

}