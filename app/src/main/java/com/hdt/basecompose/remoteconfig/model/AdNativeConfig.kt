package com.hdt.basecompose.remoteconfig.model

import com.hdt.basecompose.remoteconfig.model.type.LayoutNativeType
import com.google.gson.annotations.SerializedName

data class AdNativeConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("type_layout")
    val layout: String,
    @SerializedName("list_ads")
    val listAds: List<AdConfig>,
) {
    companion object {
        fun defaultSplash() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultLanguageLoading() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultLanguage() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultLanguageDup() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultOnboarding() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultOnboardingFull1() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.Other.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultOnboardingFull2() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.Other.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultFeature() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
        fun defaultHome() = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/2247696110"))
        )
    }
}
