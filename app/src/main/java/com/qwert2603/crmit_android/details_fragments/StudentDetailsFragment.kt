package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Parent
import com.qwert2603.crmit_android.entity.StudentFull
import com.qwert2603.crmit_android.entity_details.*

@FragmentWithArgs
class StudentDetailsFragment : EntityDetailsFragment<StudentFull>() {

    override val source = DiHolder.rest::getStudentDetails

    override val dbDaoInterface: DaoInterface<StudentFull> = DiHolder.studentFullDaoInterface

    override fun StudentFull.entityName() = fio

    override fun StudentFull.entityNameStrike() = !systemUser.enabled

    override fun StudentFull.toDetailsList(): List<EntityDetailsListItem> = listOfNotNull(
            EntityDetailsSystemInfo(systemUser.enabled, filled),
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


