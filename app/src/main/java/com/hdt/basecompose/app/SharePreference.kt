package com.hdt.basecompose.app

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharePreference(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // ── Delegates ──────────────────────────────────────────────────────────────

    fun string(key: String, default: String = "") = delegate(
        get = { prefs.getString(key, default) ?: default },
        set = { prefs.edit().putString(key, it).apply() }
    )

    fun int(key: String, default: Int = 0) = delegate(
        get = { prefs.getInt(key, default) },
        set = { prefs.edit().putInt(key, it).apply() }
    )

    fun long(key: String, default: Long = 0L) = delegate(
        get = { prefs.getLong(key, default) },
        set = { prefs.edit().putLong(key, it).apply() }
    )

    fun boolean(key: String, default: Boolean = false) = delegate(
        get = { prefs.getBoolean(key, default) },
        set = { prefs.edit().putBoolean(key, it).apply() }
    )

    fun float(key: String, default: Float = 0f) = delegate(
        get = { prefs.getFloat(key, default) },
        set = { prefs.edit().putFloat(key, it).apply() }
    )

    inline fun <reified T : Enum<T>> enum(key: String, default: T) = delegate(
        get = {
            val name = prefs.getString(key, null) ?: return@delegate default
            enumValues<T>().firstOrNull { it.name == name } ?: default
        },
        set = { prefs.edit().putString(key, it.name).apply() }
    )

    inline fun <reified T> list(key: String, default: List<T> = emptyList()) = delegate(
        get = {
            val json = prefs.getString(key, null) ?: return@delegate default
            gson.fromJson(json, object : TypeToken<List<T>>() {}.type) ?: default
        },
        set = { prefs.edit().putString(key, gson.toJson(it)).apply() }
    )

    // ── Reactive access ────────────────────────────────────────────────────────

    fun flowOf(key: String): Flow<String?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (k == key) trySend(prefs.getString(key, null))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(key, null))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun clear() = prefs.edit().clear().apply()

    fun remove(key: String) = prefs.edit().remove(key).apply()

    // ── Internal ───────────────────────────────────────────────────────────────

    private fun <T> delegate(get: () -> T, set: (T) -> Unit) =
        object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
        }
}
