package com.hdt.basecompose.remoteconfig.model.app_open

import com.hdt.basecompose.remoteconfig.model.AdConfig
import com.google.gson.annotations.SerializedName

data class AppOpenAdConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("list_ads")
    val listAds: List<AdConfig>,
) {
    companion object {
        fun defaultAll() = AppOpenAdConfig(
            enable = true,
            listAds = listOf(
                AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/9257395921"),
                AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/9257395921"),
            )
        )
    }
}
