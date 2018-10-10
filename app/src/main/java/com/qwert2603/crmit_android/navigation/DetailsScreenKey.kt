package com.qwert2603.crmit_android.navigation

import android.widget.TextView

data class DetailsScreenKey(
        val entityId: Long,
        val entityName: String,
        val entityNameTextView: TextView? = null,
        val entityNameStrike: Boolean = false,
        val entityNameColorAccent: Boolean = false
)

interface DetailsScreen {
    val key: DetailsScreenKey
}