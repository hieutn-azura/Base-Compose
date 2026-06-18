package com.hdt.basecompose.remoteconfig

import android.app.Application
import com.hdt.basecompose.remoteconfig.config.RemoteAdsConfiguration
import com.hdt.basecompose.remoteconfig.config.RemoteLogicConfiguration
import com.hdt.basecompose.remoteconfig.config.RemoteUiConfiguration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

val remoteUi get() = RemoteUiConfiguration.getInstance()
val remoteAds get() = RemoteAdsConfiguration.getInstance()
val remoteLogic get() = RemoteLogicConfiguration.getInstance()

object RemoteInitializer {
    fun init(application: Application) {
        listOf(
            RemoteAdsConfiguration.getInstance(),
            RemoteUiConfiguration.getInstance(),
            RemoteLogicConfiguration.getInstance(),
        ).forEach { it.init(application) }
    }

    private fun sync(remoteConfig: FirebaseRemoteConfig) {
        listOf(
            RemoteAdsConfiguration.getInstance(),
            RemoteUiConfiguration.getInstance(),
            RemoteLogicConfiguration.getInstance(),
        ).forEach { it.sync(remoteConfig) }
    }

    suspend fun setupRemoteConfig(isDebug: Boolean) {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(30L)
            .setMinimumFetchIntervalInSeconds(if (isDebug) 0L else 3600L)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        val isSuccess = runCatching {
            firebaseRemoteConfig.fetchAndActivate().await()
        }.getOrElse { false }
        if (isSuccess) { sync(firebaseRemoteConfig) }
    }
}
