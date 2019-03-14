package com.qwert2603.crmit_android.navigation

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter

class NavigationAdapter : BaseRecyclerViewAdapter<NavigationItem>() {
    override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = NavigationItemViewHolder(parent)

    var selectedScreen: Screen? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }
}