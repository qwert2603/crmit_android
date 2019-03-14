package com.qwert2603.crmit_android.util

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
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

abstract class ObservableField<T> {
    protected val lock = Any()

    abstract var field: T
    abstract val changes: Observable<T>

    fun updateField(updater: (T) -> T): T {
        synchronized(lock) {
            field = updater(field)
            return field
        }
    }
}

object PreferenceUtils {

    inline fun <reified T : Any> createPrefsObjectObservable(
            prefs: SharedPreferences,
            key: String,
            gson: Gson,
            defaultValue: T
    ): ObservableField<T> {
        val changes = BehaviorSubject.create<T>()

        return object : ObservableField<T>() {
            init {
                changes.onNext(field)
            }

            override var field: T
                get() =
                    if (key in prefs) {
                        try {
                            gson.fromJson<T>(prefs.getString(key, ""), object : TypeToken<T>() {}.type)
                        } catch (t: Throwable) {
                            defaultValue
                        }
                    } else {
                        defaultValue
                    }
                set(value) {
                    synchronized(lock) {
                        prefs.edit { putString(key, gson.toJson(value)) }
                        changes.onNext(value)
                    }
                }

            override val changes: Observable<T> = changes.hide()
        }
    }

}