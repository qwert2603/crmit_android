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
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.renderIfChanged
import com.qwert2603.andrlib.util.showIfNotYet
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import com.qwert2603.crmit_android.util.setStrike
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_entity_details.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class EntityDetailsFragment<E : Any> : LRFragment<EntityDetailsViewState<E>, EntityDetailsView<E>, EntityDetailsPresenter<E>>(), EntityDetailsView<E> {

    @Arg
    var entityId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var entityName: String

    @Arg
    var entityNameStrike: Boolean = false

    @Arg
    var entityNameColorAccent: Boolean = false


    protected abstract val source: (entityId: Long) -> Single<E>

    protected abstract val dbDaoInterface: DaoInterface<E>

    protected abstract fun E.entityName(): String

    protected open fun E.entityNameStrike() = false

    protected open fun EntityDetailsViewState<E>.entityNameColorAccent() = false

    protected abstract fun E.toDetailsList(): List<EntityDetailsListItem>

    private val adapter = EntityDetailsAdapter()

    override fun createPresenter() = EntityDetailsPresenter(
            entityId = entityId,
            source = source,
            dbDaoInterface = dbDaoInterface
    )

    override fun loadRefreshPanel(): LoadRefreshPanel = entityDetails_LRPanelImpl

    protected val toolbarTitleTextView: TextView get() = toolbar.getChildAt(0) as TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_entity_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = entityName
        toolbarTitleTextView.transitionName = "entity_name_$entityId"
        toolbarTitleTextView.setStrike(entityNameStrike)
        toolbarTitleTextView.setTextColor(resources.color(if (entityNameColorAccent) R.color.colorAccent else android.R.color.black))

        view.findViewById<ViewAnimator>(R.id.list_ViewAnimator).showIfNotYet(2)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh !is EntityDetailsSystemInfoViewHolder })

        toolbar.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
    }


    override fun render(vs: EntityDetailsViewState<E>) {
        super.render(vs)

        vs.entity?.let {
            toolbarTitleTextView.text = it.entityName()
            toolbarTitleTextView.setStrike(it.entityNameStrike())
            toolbarTitleTextView.setTextColor(resources.color(if (vs.entityNameColorAccent()) R.color.colorAccent else android.R.color.black))
        }

        adapter.authedUserAccountType = vs.authedUserAccountType
        adapter.authedUserDetailsId = vs.authedUserDetailsId

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