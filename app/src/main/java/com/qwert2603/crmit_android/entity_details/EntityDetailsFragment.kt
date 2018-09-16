package com.qwert2603.crmit_android.entity_details

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewAnimator
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.showIfNotYet
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_entity_details.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class EntityDetailsFragment<E : Any> : LRFragment<EntityDetailsViewState<E>, EntityDetailsView<E>, EntityDetailsPresenter<E>>(), EntityDetailsView<E> {

    abstract val entityId: Long

    abstract val source: (entityId: Long) -> Single<E>

    abstract val dbDao: DaoInterface<E>

    abstract fun E.toDetailsList(): List<EntityDetailsListItem>

    private val adapter = EntityDetailsAdapter()

    override fun createPresenter() = EntityDetailsPresenter(
            entityId = entityId,
            source = source,
            dbDao = dbDao
    )

    override fun loadRefreshPanel(): LoadRefreshPanel = entityDetails_LRPanelImpl

    override fun viewForSnackbar(): View? = entityDetails_CoordinatorLayout

    protected val toolbarTitleTextView: TextView get() = toolbar.getChildAt(0) as TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_entity_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ViewAnimator>(R.id.list_ViewAnimator).showIfNotYet(2)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh !is EntityDetailsSystemInfoViewHolder })
    }

    override fun render(vs: EntityDetailsViewState<E>) {
        super.render(vs)

        renderIfChanged({ entity }) {
            if (it == null) return@renderIfChanged
            adapter.adapterList = BaseRecyclerViewAdapter.AdapterList(it.toDetailsList())
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is EntityDetailsViewAction) return super.executeAction(va)
        when (va) {
            EntityDetailsViewAction.ShowingCachedData -> Snackbar.make(entityDetails_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
        }.also { }
    }
}