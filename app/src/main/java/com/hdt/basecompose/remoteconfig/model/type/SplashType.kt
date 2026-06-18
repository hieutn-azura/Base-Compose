package com.hdt.basecompose.remoteconfig.model.type

sealed class SplashType(val type: String) {
    data object Inter : SplashType("inter")
    data object AppOpen : SplashType("appopen")
    data object Native : SplashType("native")
}
