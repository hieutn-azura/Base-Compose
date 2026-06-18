package com.hdt.basecompose.remoteconfig.model.splash

import com.hdt.basecompose.remoteconfig.model.type.SplashType
import com.google.gson.annotations.SerializedName

data class AdSplashConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("total_timeout_ms")
    val totalTimeout: Long,
    @SerializedName("list_ads")
    val listAds: List<SplashConfig>,
) {
    companion object {
        fun defaultSplash() = AdSplashConfig(
            enable = true,
            totalTimeout = 45000,
            listAds = listOf(
                SplashConfig(enableAd = true, type = SplashType.Inter.type, timeout = 30000, adUnit = "ca-app-pub-3940256099942544/1033173712"),
                SplashConfig(enableAd = true, type = SplashType.AppOpen.type, timeout = 30000, adUnit = "ca-app-pub-3940256099942544/9257395921"),
                SplashConfig(enableAd = true, type = SplashType.Native.type, timeout = 30000, adUnit = "ca-app-pub-3940256099942544/2247696110"),
                SplashConfig(enableAd = true, type = SplashType.Inter.type, timeout = 30000, adUnit = "ca-app-pub-3940256099942544/1033173712"),
            )
        )
    }
}
