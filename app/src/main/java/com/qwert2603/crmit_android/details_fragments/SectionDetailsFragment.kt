package com.qwert2603.crmit_android.details_fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsGroupsList
import com.qwert2603.crmit_android.entity_details.EntityDetailsViewState
import com.qwert2603.crmit_android.util.toPointedString
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class SectionDetailsFragment : EntityDetailsFragment<Section>() {

    data class Key(
            val sectionId: Long,
            val sectionName: String,
            val sectionNameTextView: TextView
    )

    @Arg
    override var entityId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var sectionName: String

    override val source = DiHolder.rest::getSectionDetails

    override val dbDao = DiHolder.sectionDao

    override fun Section.toDetailsList() = listOf(
            EntityDetailsField(R.string.detailsField_name, name),
            EntityDetailsField(R.string.detailsField_price, getString(R.string.price_format, price.toPointedString())),
            EntityDetailsGroupsList(groups)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = sectionName
        toolbarTitleTextView.transitionName = "section_name_$entityId"

        super.onViewCreated(view, savedInstanceState)
    }

    override fun render(vs: EntityDetailsViewState<Section>) {
        super.render(vs)

        vs.entity?.let { toolbarTitleTextView.text = it.name }
    }
}