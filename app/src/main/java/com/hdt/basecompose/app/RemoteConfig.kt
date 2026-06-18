package com.hdt.basecompose.app

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfig {

    // Define remote config key constants here:
    // const val FEATURE_NEW_ONBOARDING = "feature_new_onboarding"

    private val rc by lazy {
        Firebase.remoteConfig.also { config ->
            config.setConfigSettingsAsync(
                remoteConfigSettings { minimumFetchIntervalInSeconds = 3600L }
            )
        }
    }

    fun fetch(onComplete: (success: Boolean) -> Unit = {}) {
        rc.fetchAndActivate().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun getString(key: String, default: String = ""): String =
        rc.getString(key).ifEmpty { default }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        rc.getBoolean(key)

    fun getLong(key: String, default: Long = 0L): Long =
        rc.getLong(key)
}
