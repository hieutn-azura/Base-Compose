package com.hdt.basecompose.remoteconfig.config

import com.hdt.basecompose.remoteconfig.BaseRemoteConfiguration
import com.hdt.basecompose.remoteconfig.model.AdBannerConfig
import com.hdt.basecompose.remoteconfig.model.AdInterConfig
import com.hdt.basecompose.remoteconfig.model.AdNativeConfig
import com.hdt.basecompose.remoteconfig.model.AdRewardConfig
import com.hdt.basecompose.remoteconfig.model.app_open.AppOpenAdConfig
import com.hdt.basecompose.remoteconfig.model.onboarding.OnboardingScreen
import com.hdt.basecompose.remoteconfig.model.splash.AdSplashConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson

class RemoteAdsConfiguration private constructor() : BaseRemoteConfiguration() {
    private val gson = Gson()

    override fun getPreferencesName() = PREFS_NAME

    override fun sync(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.saveToLocal(AdsEnable)
        remoteConfig.saveToLocal(MetaCtrLow)
        remoteConfig.saveToLocal(SplashConfig)
        remoteConfig.saveToLocal(NativeSplashConfig)
        remoteConfig.saveToLocal(NativeLanguageLoadingConfig)
        remoteConfig.saveToLocal(NativeLanguageConfig)
        remoteConfig.saveToLocal(NativeLanguageDupConfig)
        remoteConfig.saveToLocal(NativeOnboardingConfig)
        remoteConfig.saveToLocal(OnboardingScreenConfig)
        remoteConfig.saveToLocal(NativeOnboardingFull1Config)
        remoteConfig.saveToLocal(NativeOnboardingFull2Config)
        remoteConfig.saveToLocal(NativeFeatureConfig)
        remoteConfig.saveToLocal(NativeFeatureDupConfig)
        remoteConfig.saveToLocal(NativeHomeConfig)
        remoteConfig.saveToLocal(InterAllConfig)
        remoteConfig.saveToLocal(BannerAllConfig)
        remoteConfig.saveToLocal(RewardAllConfig)
        remoteConfig.saveToLocal(OpenAdConfig)
        remoteConfig.saveToLocal(LockscreenSchedulesConfig)
    }

    private data object AdsEnable : RemoteKeys.BooleanKey("ad_enable", true)
    private data object MetaCtrLow : RemoteKeys.BooleanKey("meta_ctr_low", true)
    private data object OnboardingScreenConfig : RemoteKeys.StringKey("onboarding_config", Gson().toJson(OnboardingScreen.default()))
    private data object SplashConfig : RemoteKeys.StringKey("I101_config_1", Gson().toJson(AdSplashConfig.defaultSplash()))
    private data object NativeSplashConfig : RemoteKeys.StringKey("N101_config_1", Gson().toJson(AdNativeConfig.defaultSplash()))
    private data object NativeLanguageLoadingConfig : RemoteKeys.StringKey("N102_config_1", Gson().toJson(AdNativeConfig.defaultLanguageLoading()))
    private data object NativeLanguageConfig : RemoteKeys.StringKey("N103_config_1", Gson().toJson(AdNativeConfig.defaultLanguage()))
    private data object NativeLanguageDupConfig : RemoteKeys.StringKey("N104_config_1", Gson().toJson(AdNativeConfig.defaultLanguageDup()))
    private data object NativeOnboardingConfig : RemoteKeys.StringKey("N105_config_1", Gson().toJson(AdNativeConfig.defaultOnboarding()))
    private data object NativeOnboardingFull1Config : RemoteKeys.StringKey("N107_config_1", Gson().toJson(AdNativeConfig.defaultOnboardingFull1()))
    private data object NativeOnboardingFull2Config : RemoteKeys.StringKey("N108_config_1", Gson().toJson(AdNativeConfig.defaultOnboardingFull2()))
    private data object NativeFeatureConfig : RemoteKeys.StringKey("N109_config_1", Gson().toJson(AdNativeConfig.defaultFeature()))
    private data object NativeFeatureDupConfig : RemoteKeys.StringKey("N109_config_2", Gson().toJson(AdNativeConfig.defaultFeatureDup()))
    private data object NativeHomeConfig : RemoteKeys.StringKey("N110_config_1", Gson().toJson(AdNativeConfig.defaultHome()))
    private data object InterAllConfig : RemoteKeys.StringKey("I102_config_1", Gson().toJson(AdInterConfig.defaultAll()))
    private data object BannerAllConfig : RemoteKeys.StringKey("B101_config_1", Gson().toJson(AdBannerConfig.defaultAll()))
    private data object RewardAllConfig : RemoteKeys.StringKey("R101_config_1", Gson().toJson(AdRewardConfig.defaultAll()))
    private data object OpenAdConfig : RemoteKeys.StringKey("A101_config_1", Gson().toJson(AppOpenAdConfig.defaultAll()))
    private data object LockscreenSchedulesConfig : RemoteKeys.StringKey("time_noti_reminder", "03:00,08:00,13:00,18:00,23:00")

    val isAdsEnable: Boolean get() = AdsEnable.get()
    val metaCtrLow: Boolean get() = MetaCtrLow.get()

    val adSplashConfig: AdSplashConfig
        get() = gson.fromJsonConfig(SplashConfig, AdSplashConfig.defaultSplash()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeSplashConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeSplashConfig, AdNativeConfig.defaultSplash()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeLanguageLoadingConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeLanguageLoadingConfig, AdNativeConfig.defaultLanguageLoading()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeLanguageConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeLanguageConfig, AdNativeConfig.defaultLanguage()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeLanguageDupConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeLanguageDupConfig, AdNativeConfig.defaultLanguageDup()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeOnboardingConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeOnboardingConfig, AdNativeConfig.defaultOnboarding()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeOnboardingFull1Config: AdNativeConfig
        get() = gson.fromJsonConfig(NativeOnboardingFull1Config, AdNativeConfig.defaultOnboardingFull1()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeOnboardingFull2Config: AdNativeConfig
        get() = gson.fromJsonConfig(NativeOnboardingFull2Config, AdNativeConfig.defaultOnboardingFull2()) { it.copy(enable = it.enable && isAdsEnable) }

    val onboardingScreenConfig: OnboardingScreen
        get() = gson.fromJsonConfig(OnboardingScreenConfig, OnboardingScreen.default()) {
            it.copy(
                isEnableScreen1 = it.isEnableScreen1 && isAdsEnable,
                isEnableScreen2 = it.isEnableScreen2 && isAdsEnable,
                isEnableScreen3 = it.isEnableScreen3 && isAdsEnable,
                isEnableScreen4 = it.isEnableScreen4 && isAdsEnable,
            )
        }

    val adNativeFeatureConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeFeatureConfig, AdNativeConfig.defaultFeature()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeFeatureDupConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeFeatureDupConfig, AdNativeConfig.defaultFeatureDup()) { it.copy(enable = it.enable && isAdsEnable) }

    val adNativeHomeConfig: AdNativeConfig
        get() = gson.fromJsonConfig(NativeHomeConfig, AdNativeConfig.defaultHome()) { it.copy(enable = it.enable && isAdsEnable) }

    val adInterAllConfig: AdInterConfig
        get() = gson.fromJsonConfig(InterAllConfig, AdInterConfig.defaultAll()) { it.copy(enable = it.enable && isAdsEnable) }

    val adBannerAllConfig: AdBannerConfig
        get() = gson.fromJsonConfig(BannerAllConfig, AdBannerConfig.defaultAll()) { it.copy(enable = it.enable && isAdsEnable) }

    val adRewardAllConfig: AdRewardConfig
        get() = gson.fromJsonConfig(RewardAllConfig, AdRewardConfig.defaultAll()) { it.copy(enable = it.enable && isAdsEnable) }

    val appOpenAdConfig: AppOpenAdConfig
        get() = gson.fromJsonConfig(OpenAdConfig, AppOpenAdConfig.defaultAll()) { it.copy(enable = it.enable && isAdsEnable) }

    val lockscreenNotificationSchedules: List<Pair<Int, Int>>
        get() = runCatching {
            LockscreenSchedulesConfig.get()
                .split(",")
                .mapNotNull { timeStr ->
                    val parts = timeStr.trim().split(":")
                    if (parts.size == 2) {
                        val hour = parts[0].toIntOrNull()
                        val minute = parts[1].toIntOrNull()
                        if (hour != null && minute != null && hour in 0..23 && minute in 0..59) Pair(hour, minute) else null
                    } else null
                }
        }.getOrElse { emptyList() }

    companion object {
        private const val PREFS_NAME = "remote_config_ads_prefs"

        @Volatile
        private var _instance: RemoteAdsConfiguration? = null

        fun getInstance(): RemoteAdsConfiguration =
            _instance ?: synchronized(this) {
                _instance ?: RemoteAdsConfiguration().also { _instance = it }
            }
    }
}
