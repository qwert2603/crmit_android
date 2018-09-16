package com.qwert2603.crmit_android.details_fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Parent
import com.qwert2603.crmit_android.entity.StudentFull
import com.qwert2603.crmit_android.entity_details.*
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class StudentDetailsFragment : EntityDetailsFragment<StudentFull>() {

    data class Key(
            val studentId: Long,
            val studentFio: String,
            val systemUserEnabled: Boolean,
            val studentFioTextView: TextView
    )

    @Arg
    override var entityId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var studentFio: String

    @Arg
    var systemUserEnabled: Boolean = true

    override val source = DiHolder.rest::getStudentDetails

    override val dbDao: DaoInterface<StudentFull> = DiHolder.studentFullDao

    override fun StudentFull.toDetailsList(): List<EntityDetailsListItem> = listOfNotNull(
            EntityDetailsStudentSystemInfo(systemUser.enabled, filled),
            EntityDetailsField(R.string.detailsField_login, systemUser.login, R.drawable.ic_person_black_24dp),
            EntityDetailsField(R.string.detailsField_lessonsAttendedCount, lessonsAttendedCount.toString()),
            EntityDetailsField(R.string.detailsField_birthDate, showingBirthDate()),
            EntityDetailsField(R.string.detailsField_birthPlace, birthPlace),
            EntityDetailsField(R.string.detailsField_registrationPlace, registrationPlace),
            EntityDetailsField(R.string.detailsField_actualAddress, actualAddress),
            additionalInfo?.takeIf { it.isNotEmpty() }?.let { EntityDetailsField(R.string.detailsField_additionalInfo, it) },
            knownFrom?.takeIf { it.isNotEmpty() }?.let { EntityDetailsField(R.string.detailsField_knownFrom, it) },
            EntityDetailsField(R.string.detailsField_school, getString(R.string.student_school_format, school.name, grade, shift)),
            phone?.let { EntityDetailsField(R.string.detailsField_phone, it, R.drawable.ic_local_phone_black_24dp) },
            EntityDetailsField(R.string.detailsField_contactPhone, "$contactPhoneNumber ($contactPhoneWho)", R.drawable.ic_local_phone_black_24dp),
            EntityDetailsField(R.string.detailsField_citizenshipName, citizenshipName),
            mother?.let { EntityDetailsField(R.string.detailsField_mother, it.toTextFieldValue()) },
            father?.let { EntityDetailsField(R.string.detailsField_father, it.toTextFieldValue()) },
            EntityDetailsGroupsList(groups)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = studentFio
        toolbarTitleTextView.transitionName = "student_fio_$entityId"
        toolbarTitleTextView.setStrike(!systemUserEnabled)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun render(vs: EntityDetailsViewState<StudentFull>) {
        super.render(vs)

        vs.entity?.let {
            toolbarTitleTextView.text = it.fio
            toolbarTitleTextView.setStrike(!it.systemUser.enabled)
        }
    }

    private fun Parent.toTextFieldValue() = listOf(
            R.string.detailsField_fio to fio,
            R.string.detailsField_phone to phone,
            R.string.detailsField_address to address,
            R.string.detailsField_email to email,
            R.string.detailsField_vkLink to vkLink,
            R.string.detailsField_homePhone to homePhone,
            R.string.detailsField_notificationTypesString to notificationTypesString.takeIf { it.isNotEmpty() }?.reduce { acc, s -> "$acc / $s" }
    )
            .filter { it.second != null }
            .map { "${getString(it.first)}: ${it.second}" }
            .reduce { acc, s -> "$acc\n$s" }
}


