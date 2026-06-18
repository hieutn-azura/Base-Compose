package com.hdt.basecompose.remoteconfig.model

import com.google.gson.annotations.SerializedName

data class AdBannerConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("is_collapsible")
    val isCollapsible: Boolean,
    @SerializedName("list_ads")
    val listAds: List<AdConfig>,
) {
    companion object {
        fun defaultAll() = AdBannerConfig(
            enable = true,
            isCollapsible = true,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/6300978111"))
        )
    }
}
