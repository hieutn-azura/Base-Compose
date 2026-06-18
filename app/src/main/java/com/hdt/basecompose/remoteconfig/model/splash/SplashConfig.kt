package com.hdt.basecompose.remoteconfig.model.splash

import com.google.gson.annotations.SerializedName

data class SplashConfig(
    @SerializedName("enable_ad")
    val enableAd: Boolean,
    @SerializedName("type")
    val type: String,
    @SerializedName("timeout_ms")
    val timeout: Long,
    @SerializedName("adunit")
    val adUnit: String,
)
