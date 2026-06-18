package com.hdt.basecompose.remoteconfig

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.hdt.basecompose.remoteconfig.config.RemoteEnumString
import com.hdt.basecompose.remoteconfig.config.RemoteKeys
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken


abstract class BaseRemoteConfiguration {
    private lateinit var application: Application

    internal abstract fun getPreferencesName(): String

    abstract fun sync(remoteConfig: FirebaseRemoteConfig)

    private fun getPreferences(): SharedPreferences {
        return application.getSharedPreferences(getPreferencesName(), Context.MODE_PRIVATE)
    }

    fun init(application: Application) {
        this.application = application
    }

    internal fun FirebaseRemoteConfig.saveToLocal(keyType: RemoteKeys) {
        val remoteConfig = this
        getPreferences().edit {
            val key = keyType.remoteKey
            when (keyType) {
                is RemoteKeys.BooleanKey -> {
                    putBoolean(key, runCatching { remoteConfig.getBoolean(key) }.getOrElse { keyType.defaultValue })
                }
                is RemoteKeys.StringKey -> {
                    putString(key, runCatching { remoteConfig.getString(key) }.getOrElse { keyType.defaultValue })
                }
                is RemoteKeys.LongKey -> {
                    putLong(key, runCatching { remoteConfig.getLong(key) }.getOrElse { keyType.defaultValue })
                }
                is RemoteKeys.DoubleKey -> {
                    putFloat(key, runCatching { remoteConfig.getDouble(key) }.getOrElse { keyType.defaultValue }.toFloat())
                }
                is RemoteKeys.ListIntegerKey -> {
                    putString(key, runCatching { remoteConfig.getString(key) }.getOrElse { keyType.defaultValue.joinToString(",") })
                }
                is RemoteKeys.StringEnumKey<*> -> {
                    putString(key, runCatching { remoteConfig.getString(key) }.getOrElse { keyType.defaultValue.remoteValue })
                }
            }
        }
    }

    internal fun RemoteKeys.BooleanKey.get(): Boolean =
        runCatching { getPreferences().getBoolean(remoteKey, defaultValue) }.getOrDefault(defaultValue)

    internal fun RemoteKeys.StringKey.get(): String =
        runCatching { getPreferences().getString(remoteKey, defaultValue).takeUnless { it.isNullOrBlank() } }
            .getOrNull() ?: defaultValue

    internal fun RemoteKeys.LongKey.get(): Long =
        runCatching { getPreferences().getLong(remoteKey, defaultValue) }.getOrDefault(defaultValue)

    internal fun RemoteKeys.DoubleKey.get(): Double =
        runCatching { getPreferences().getFloat(remoteKey, defaultValue.toFloat()) }.getOrDefault(defaultValue).toDouble()

    internal fun RemoteKeys.ListIntegerKey.get(): List<Int> =
        runCatching {
            getPreferences().getString(remoteKey, defaultValue.joinToString(","))
                ?.split(",")?.mapNotNull { it.toIntOrNull() }
        }.getOrNull() ?: defaultValue

    internal inline fun <reified T> RemoteKeys.StringEnumKey<T>.get(): T where T : RemoteEnumString, T : Enum<T> =
        runCatching {
            val stringValue = getPreferences().getString(remoteKey, defaultValue.remoteValue)
                .takeUnless { it.isNullOrBlank() } ?: defaultValue.remoteValue
            enumValues<T>().find { it.remoteValue == stringValue } ?: defaultValue
        }.getOrDefault(defaultValue)

    internal inline fun <reified T> Gson.fromJsonConfig(
        key: RemoteKeys.StringKey,
        default: T,
        crossinline validate: (T) -> Boolean = { true },
        crossinline adjust: (T) -> T = { it }
    ): T {
        return runCatching {
            val raw = key.get()
            if (raw.isBlank()) return default
            val expectedKeys = JsonParser.parseString(key.defaultValue).asJsonObject.keySet()
            val actualKeys = JsonParser.parseString(raw).asJsonObject.keySet()
            if (!actualKeys.containsAll(expectedKeys)) return@runCatching adjust(default)
            val type = object : TypeToken<T>() {}.type
            val parsed = fromJson<T>(raw, type) ?: return@runCatching adjust(default)
            if (!validate(parsed)) return@runCatching adjust(default)
            adjust(parsed)
        }.getOrElse { adjust(default) }
    }
}
