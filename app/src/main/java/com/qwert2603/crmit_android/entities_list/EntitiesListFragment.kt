package com.qwert2603.crmit_android.entities_list

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.PluralsRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListFragment
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.base.recyclerview.vh.PageIndicatorViewHolder
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_entities_list.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class EntitiesListFragment<E : IdentifiableLong> : ListFragment<EntitiesListViewState<E>, EntitiesListView<E>, EntitiesListPresenter<E>, E>(), EntitiesListView<E> {

    abstract val source: (offset: Int, count: Int, search: String) -> Single<List<E>>

    @get:StringRes
    abstract val titleRes: Int

    @get:LayoutRes
    abstract val vhLayoutRes: Int

    @PluralsRes
    open val entityPluralsRes: Int = R.plurals.items

    open val pageSize: Int = 1

    abstract fun View.bindEntity(e: E)

    override val adapter = object : BaseRecyclerViewAdapter<E>() {
        override fun pluralsRes() = entityPluralsRes

        override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = object : BaseRecyclerViewHolder<E>(parent, vhLayoutRes) {
            override fun bind(m: E) {
                super.bind(m)
                itemView.bindEntity(m)
            }
        }
    }

    override fun createPresenter() = EntitiesListPresenter(
            uiSchedulerProvider = DiHolder.uiSchedulerProvider,
            modelSchedulersProvider = DiHolder.modelSchedulersProvider,
            source = source,
            pageSize = pageSize
    )

    override fun loadRefreshPanel(): LoadRefreshPanel = entities_LRPanelImpl

    override fun viewForSnackbar(): View? = entities_LRPanelImpl

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_entities_list)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _list_RecyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh !is PageIndicatorViewHolder })

        toolbar.setTitle(titleRes)
        toolbar.setOnClickListener { _list_RecyclerView.smoothScrollToPosition(0) }

        super.onViewCreated(view, savedInstanceState)
    }
}