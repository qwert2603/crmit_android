package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.util.toPointedString
import io.reactivex.Single
import kotlinx.android.synthetic.main.item_section.view.*

class SectionsListFragment : EntitiesListFragment<Section>() {
    override val source: Single<List<Section>> = DiHolder.rest.getSectionsList()

    override val titleRes = R.string.title_sections

    override val vhLayoutRes = R.layout.item_section

    override val entityPluralsRes = R.plurals.sections

    override fun View.bindEntity(e: Section) {
        name_TextView.text = e.name
        price_TextView.text = getString(R.string.price_format, e.price.toPointedString())
        groups_TextView.setVisible(e.groups.isNotEmpty())
        if (e.groups.isNotEmpty()) {
            groups_TextView.text = e.groups
                    .map { "* ${it.name}" }
                    .reduce { acc, s -> "$acc\n$s" }
        }
    }
}