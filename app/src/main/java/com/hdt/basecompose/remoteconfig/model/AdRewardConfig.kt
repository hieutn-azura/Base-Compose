package com.hdt.basecompose.remoteconfig.model

import com.google.gson.annotations.SerializedName

data class AdRewardConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("list_ads")
    val listAds: List<AdConfig>,
) {
    companion object {
        fun defaultAll() = AdRewardConfig(
            enable = true,
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/5224354917"))
        )
    }
}
