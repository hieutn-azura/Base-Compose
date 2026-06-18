package com.hdt.basecompose.remoteconfig.config

import com.hdt.basecompose.remoteconfig.BaseRemoteConfiguration
import com.hdt.basecompose.remoteconfig.model.language.LanguageLoading
import com.hdt.basecompose.remoteconfig.model.native_full.NativeFullConfig
import com.hdt.basecompose.remoteconfig.model.splash.SplashTimeout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson

class RemoteLogicConfiguration private constructor() : BaseRemoteConfiguration() {
    private val gson = Gson()

    override fun getPreferencesName() = PREFS_NAME

    override fun sync(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.saveToLocal(InAppUpdate)
        remoteConfig.saveToLocal(TimesShowUpdate)
        remoteConfig.saveToLocal(LanguageLoadingConfig)
        remoteConfig.saveToLocal(SplashTimeOutConfig)
        remoteConfig.saveToLocal(NativeFullSplashConfig)
        remoteConfig.saveToLocal(NativeFullOnb1Config)
        remoteConfig.saveToLocal(NativeFullOnb2Config)
    }

    private data object InAppUpdate : RemoteKeys.StringKey("update_app", "off_pop_up_update")
    private data object TimesShowUpdate : RemoteKeys.LongKey("update_app_times", 3)

    val inAppUpdate: String get() = InAppUpdate.get()
    val timesShowUpdate: Int get() = TimesShowUpdate.get().toInt()

    private data object SplashTimeOutConfig : RemoteKeys.StringKey(
        "splash_timeout", Gson().toJson(SplashTimeout.default())
    )
    private data object LanguageLoadingConfig : RemoteKeys.StringKey(
        "language_loading_config", Gson().toJson(LanguageLoading.default())
    )
    private data object NativeFullSplashConfig : RemoteKeys.StringKey(
        "N106_config_2", Gson().toJson(NativeFullConfig.defaultSplash())
    )
    private data object NativeFullOnb1Config : RemoteKeys.StringKey(
        "N107_config_2", Gson().toJson(NativeFullConfig.defaultFull1())
    )
    private data object NativeFullOnb2Config : RemoteKeys.StringKey(
        "N108_config_2", Gson().toJson(NativeFullConfig.defaultFull2())
    )

    val splashTimeout: SplashTimeout
        get() = gson.fromJsonConfig(SplashTimeOutConfig, SplashTimeout.default())

    val languageLoadingConfig: LanguageLoading
        get() = gson.fromJsonConfig(LanguageLoadingConfig, LanguageLoading.default())

    val nativeFullSplashConfig: NativeFullConfig
        get() = gson.fromJsonConfig(NativeFullSplashConfig, NativeFullConfig.defaultSplash())

    val nativeFullOnb1Config: NativeFullConfig
        get() = gson.fromJsonConfig(NativeFullOnb1Config, NativeFullConfig.defaultFull1())

    val nativeFullOnb2Config: NativeFullConfig
        get() = gson.fromJsonConfig(NativeFullOnb2Config, NativeFullConfig.defaultFull2())

    companion object {
        private const val PREFS_NAME = "remote_config_logic_prefs"

        @Volatile
        private var _instance: RemoteLogicConfiguration? = null

        fun getInstance(): RemoteLogicConfiguration =
            _instance ?: synchronized(this) {
                _instance ?: RemoteLogicConfiguration().also { _instance = it }
            }
    }
}
