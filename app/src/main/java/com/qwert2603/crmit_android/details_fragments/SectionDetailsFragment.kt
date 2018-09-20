package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsGroupsList
import com.qwert2603.crmit_android.util.toPointedString

@FragmentWithArgs
class SectionDetailsFragment : EntityDetailsFragment<Section>() {

    override val source = DiHolder.rest::getSectionDetails

    override val dbDaoInterface = DiHolder.sectionDaoInterface

    override fun Section.entityName() = name

    override fun Section.toDetailsList() = listOf(
            EntityDetailsField(R.string.detailsField_price, getString(R.string.price_format, price.toPointedString())),
            EntityDetailsGroupsList(groups)
    )
}