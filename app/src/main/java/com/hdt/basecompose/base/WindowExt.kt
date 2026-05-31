package com.hdt.basecompose.base

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowCompat

fun Activity.keepScreenOn(enabled: Boolean) {
    if (enabled) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.setFullscreen(fullscreen: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(window, !fullscreen)
}
