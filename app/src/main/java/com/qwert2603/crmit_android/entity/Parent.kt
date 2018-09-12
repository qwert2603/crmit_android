package com.qwert2603.crmit_android.entity

data class Parent(
        val id: Long,
        val fio: String,
        val phone: String,
        val address: String?,
        val email: String?,
        val vkLink: String?,
        val homePhone: String?,
        val notificationTypesString: List<String>
)