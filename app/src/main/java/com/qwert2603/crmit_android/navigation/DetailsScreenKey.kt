package com.qwert2603.crmit_android.navigation

import android.widget.TextView
import java.io.Serializable

data class DetailsScreenKey(
        val entityId: Long,
        val entityName: String,
        @Transient val entityNameTextView: TextView? = null,
        val entityNameStrike: Boolean = false,
        val entityNameColorAccent: Boolean = false
) : Serializable

interface DetailsScreen {
    val key: DetailsScreenKey
}