package com.qwert2603.crmit_android.entities_list

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.PluralsRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.*
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListFragment
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.base.recyclerview.vh.PageIndicatorViewHolder
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.navigation.BackPressListener
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_entities_list.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class EntitiesListFragment<E : IdentifiableLong>
    : ListFragment<EntitiesListViewState<E>, EntitiesListView<E>, EntitiesListPresenter<E>, E>(), EntitiesListView<E>, BackPressListener {

    abstract val source: (offset: Int, count: Int, search: String) -> Single<List<E>>

    abstract val dbDao: DaoInterface<E>

    @get:StringRes
    abstract val titleRes: Int

    @get:LayoutRes
    abstract val vhLayoutRes: Int

    @PluralsRes
    open val entityPluralsRes: Int = R.plurals.items

    open val pageSize: Int = 30

    abstract fun View.bindEntity(e: E)

    override val adapter = object : BaseRecyclerViewAdapter<E>() {
        override fun pluralsRes() = entityPluralsRes

        override fun onCreateViewHolderModel(parent: ViewGroup, viewType: Int) = object : BaseRecyclerViewHolder<E>(parent, vhLayoutRes), LayoutContainer {
            override val containerView: View = itemView

            override fun bind(m: E) {
                super.bind(m)
                itemView.bindEntity(m)
            }
        }
    }

    private val openSearchClicks = PublishSubject.create<Any>()
    private val closeSearchFromBackPress = PublishSubject.create<Any>()

    override fun createPresenter() = EntitiesListPresenter(
            source = source,
            dbDao = dbDao,
            pageSize = pageSize
    )

    override fun loadRefreshPanel(): LoadRefreshPanel = entities_LRPanelImpl

    override fun viewForSnackbar(): View? = entities_CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_entities_list)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _list_RecyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh !is PageIndicatorViewHolder })

        toolbar.setTitle(titleRes)
        toolbar.setOnClickListener { _list_RecyclerView.smoothScrollToPosition(0) }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.entities_list, menu)
        menu.findItem(R.id.search).setOnMenuItemClickListener { openSearchClicks.onNext(Any());true }
    }

    override fun onBackPressed(): Boolean = entities_SearchUI.isOpen()
            .also { if (it) closeSearchFromBackPress.onNext(Any()) }

    override fun openSearchClicks(): Observable<Any> = openSearchClicks

    override fun closeSearchClicks(): Observable<Any> = Observable.merge(
            entities_SearchUI.closeClicks(),
            closeSearchFromBackPress
    )

    override fun searchQueryChanges(): Observable<String> = entities_SearchUI.queryChanges()

    override fun render(vs: EntitiesListViewState<E>) {
        super.render(vs)

        if (vs.showingList.isEmpty()) {
            _listEmpty_TextView.setText(if (vs.searchQuery.isNotEmpty()) R.string.text_nothing_found else R.string.empty_list_text)
        }

        renderIfChanged({ searchOpen }) { entities_SearchUI.setOpen(it, prevViewState != null) }
        renderIfChanged({ searchQuery }) { entities_SearchUI.setQuery(it) }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is EntitiesListViewAction) return super.executeAction(va)
        when (va) {
            EntitiesListViewAction.ShowingCachedData -> Snackbar.make(entities_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
        }.also { }
    }
}