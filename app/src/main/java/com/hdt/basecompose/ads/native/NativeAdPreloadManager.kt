package com.hdt.basecompose.ads.`native`

import android.app.Activity
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.helper.adnative.preload.NativeAdPreload
import com.ads.control.helper.adnative.preload.NativePreloadState
import com.hdt.basecompose.BuildConfig
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

internal object NativeAdPreloadManager {
    private val preloadInstance: NativeAdPreload by lazy { NativeAdPreload.getInstance() }
    private var activityRef: WeakReference<Activity>? = null

    fun getAvailableAdCount(placement: NativePlacement): Int {
        return if (placement.canShowAds()) {
            preloadInstance.getNativeAdBuffer(
                listId = placement.listId(),
                layoutId = placement.preloadLayoutId()
            ).size
        } else 0
    }

    fun getAdPreloadState(placement: NativePlacement): StateFlow<NativePreloadState> {
        return preloadInstance.getAdPreloadState(
            listId = placement.listId(),
            layoutId = placement.preloadLayoutId()
        )
    }

    fun pollAdNative(placement: NativePlacement): ApNativeAd? {
        if (!placement.canShowAds()) return null
        return preloadInstance.pollAdNative(
            listId = placement.listId(),
            layoutId = placement.preloadLayoutId()
        )
    }

    fun preloadAd(activity: Activity, placement: NativePlacement, buffer: Int = 1) {
        if (!placement.canShowAds()) return
        activityRef = WeakReference(activity)
        handleInternalPreload(placement, buffer)
    }

    fun safePreloadAd(activity: Activity, placement: NativePlacement, buffer: Int = 1) {
        if (!placement.canShowAds()) return
        activityRef = WeakReference(activity)
        preloadIfSafe(placement, buffer)
    }

    private fun preloadIfSafe(placement: NativePlacement, buffer: Int) {
        val isDebug = BuildConfig.build_debug
        val shouldPreload = isNativeQueueEmptyAndNoPreloadActionInProgress(placement)
        if (!isDebug && !shouldPreload) return
        handleInternalPreload(placement, buffer)
    }

    private fun isNativeQueueEmptyAndNoPreloadActionInProgress(placement: NativePlacement): Boolean {
        return preloadInstance.isPreloadAvailable(
            listId = placement.listId(),
            layoutId = placement.preloadLayoutId()
        ).not()
    }

    private fun handleInternalPreload(placement: NativePlacement, buffer: Int) {
        activityRef?.get()?.let { activity ->
            preloadInstance.preload(
                activity = activity,
                listId = placement.listId(),
                layoutId = placement.preloadLayoutId(),
                buffer = buffer
            )
        }
    }

    fun cleanup() {
        activityRef?.clear()
        activityRef = null
    }
}
