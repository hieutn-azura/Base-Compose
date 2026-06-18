package com.hdt.basecompose.remoteconfig.model

import com.google.gson.annotations.SerializedName

data class AdInterConfig(
    @SerializedName("enable")
    val enable: Boolean,
    @SerializedName("time_interval_ms")
    val timeInterval: Long,
    @SerializedName("time_steps")
    val timeSteps: List<Int>,
    @SerializedName("list_ads")
    val listAds: List<AdConfig>,
) {
    companion object {
        fun defaultAll() = AdInterConfig(
            enable = true,
            timeInterval = 30000,
            timeSteps = listOf(1, 2, 3, 4, 5, 6),
            listAds = listOf(AdConfig(enableAd = true, adUnit = "ca-app-pub-3940256099942544/1033173712"))
        )
    }
}
