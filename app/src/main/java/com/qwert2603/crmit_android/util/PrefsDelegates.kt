package com.qwert2603.crmit_android.util

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun SharedPreferences.makeEdit(crossinline editAction: SharedPreferences.Editor.() -> Unit) {
    this.edit()
            .apply(editAction)
            .apply()
}

class PrefsBoolean(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Boolean = false
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean = prefs.getBoolean(key, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        prefs.makeEdit { putBoolean(key, value) }
    }
}

class PrefsInt(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Int = 0
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int = prefs.getInt(key, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        prefs.makeEdit { putInt(key, value) }
    }
}

class PrefsLong(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Long = 0L
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long = prefs.getLong(key, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        prefs.makeEdit { putLong(key, value) }
    }
}

class PrefsLongNullable(
        private val prefs: SharedPreferences,
        private val key: String
) : ReadWriteProperty<Any, Long?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long? =
            if (key in prefs) {
                prefs.getLong(key, 0)
            } else {
                null
            }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) {
        prefs.makeEdit {
            if (value != null) {
                putLong(key, value)
            } else {
                remove(key)
            }
        }
    }
}

class PrefsStringNullable(
        private val prefs: SharedPreferences,
        private val key: String
) : ReadWriteProperty<Any, String?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String? =
            if (key in prefs) {
                prefs.getString(key, "")
            } else {
                null
            }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        prefs.makeEdit {
            if (value != null) {
                putString(key, value)
            } else {
                remove(key)
            }
        }
    }
}
