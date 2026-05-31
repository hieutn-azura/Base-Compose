package com.hdt.basecompose.base

import android.os.Build

fun apiAtLeast(version: Int) = Build.VERSION.SDK_INT >= version

inline fun ifApi(version: Int, block: () -> Unit) {
    if (apiAtLeast(version)) block()
}

inline fun <T> apiOrElse(version: Int, block: () -> T, fallback: () -> T): T =
    if (apiAtLeast(version)) block() else fallback()
