package com.qwert2603.crmit_android.navigation

import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_navigation_menu.view.*

class NavigationItemViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<NavigationItem>(parent, R.layout.item_navigation_menu), LayoutContainer {

    override val containerView: View = itemView

    override fun bind(m: NavigationItem) = with(itemView) {
        super.bind(m)
        icon_ImageView.setImageResource(m.iconRes)
        title_TextView.setText(m.titleRes)
        val selected = m.screen == (adapter as? NavigationAdapter)?.selectedScreen
        itemView.isSelected = selected
        val tintColor = resources.color(if (selected) R.color.colorAccent else android.R.color.black)
        title_TextView.setTextColor(tintColor)
        icon_ImageView.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
    }

}