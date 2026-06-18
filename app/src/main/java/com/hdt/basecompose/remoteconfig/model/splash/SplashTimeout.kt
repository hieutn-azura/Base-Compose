package com.hdt.basecompose.remoteconfig.model.splash

import com.google.gson.annotations.SerializedName

data class SplashTimeout(
    @SerializedName("delay_ms")
    val delayMs: Long,
    @SerializedName("waiting_ms")
    val waitingMs: Long,
) {
    companion object {
        fun default() = SplashTimeout(delayMs = 1000, waitingMs = 5000)
    }
}
