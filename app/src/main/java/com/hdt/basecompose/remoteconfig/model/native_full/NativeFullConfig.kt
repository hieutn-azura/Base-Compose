package com.hdt.basecompose.remoteconfig.model.native_full

import com.google.gson.annotations.SerializedName

data class NativeFullConfig(
    @SerializedName("is_show_close")
    val isShowClose: Boolean,
    @SerializedName("delay_show_close_ms")
    val delayShowClose: Long,
    @SerializedName("time_delay_skip_ms")
    val timeDelaySkip: Long,
) {
    companion object {
        fun defaultSplash() = NativeFullConfig(isShowClose = true, delayShowClose = 0, timeDelaySkip = 6000)
        fun defaultFull1() = NativeFullConfig(isShowClose = false, delayShowClose = 2000, timeDelaySkip = 0)
        fun defaultFull2() = NativeFullConfig(isShowClose = true, delayShowClose = 2000, timeDelaySkip = 3000)
    }
}
