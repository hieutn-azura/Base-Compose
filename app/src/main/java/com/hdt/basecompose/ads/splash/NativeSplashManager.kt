package com.hdt.basecompose.ads.splash

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.remoteconfig.remoteAds
import com.facebook.shimmer.ShimmerFrameLayout

interface AdSplashCompleteListener {
    fun onAdComplete(adName: String)
}

class NativeSplashManager(
    private val activity: AppCompatActivity,
    private val adContainer: FrameLayout,
    private val shimmerView: ShimmerFrameLayout,
    private val listener: AdSplashCompleteListener,
) : DefaultLifecycleObserver {
    companion object {
        const val TAG = "NativeSplashManager"
    }

    private var isCleanedUp = false
    private var isCallAdComplete = false

    init {
        activity.lifecycle.addObserver(this)
    }

    private val nativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = activity,
            config = NativePlacement.SPLASH,
            lifecycleOwner = activity,
            adContainer = { adContainer },
            shimmerView = { shimmerView },
        )
    }

    fun loadNative() {
        if (remoteAds.adNativeSplashConfig.enable) {
            adContainer.visibility = android.view.View.VISIBLE
            nativeAdsWrapper.apply {
                setupNativeAd(TAG)
                registerAdCallbacks(
                    onImpression = { if (isCleanedUp) return@registerAdCallbacks; onAdComplete() },
                    onFailed = { if (isCleanedUp) return@registerAdCallbacks; onAdComplete() },
                )
                requestAds()
            }
        } else {
            adContainer.visibility = android.view.View.GONE
            onAdComplete()
        }
    }

    fun cleanup() {
        isCleanedUp = true
        activity.lifecycle.removeObserver(this)
    }

    private fun onAdComplete() {
        if (isCallAdComplete) return
        isCallAdComplete = true
        listener.onAdComplete(TAG)
    }
}
