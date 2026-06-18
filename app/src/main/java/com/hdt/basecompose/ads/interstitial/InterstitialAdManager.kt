package com.hdt.basecompose.ads.interstitial

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData

object InterstitialAdManager {
    var isCloseInterSplash = MutableLiveData(false)
    private var interAll = InterstitialAdsWrapper(InterstitialPlacement.INTER_ALL)

    fun resetInterAllConfig() { interAll.resetConfig() }
    fun updateTimeShowInterAll() { interAll.updateTimeShowInter() }
    fun loadInterAll(context: Context) { interAll.preloadAd(context) }

    fun showInterAll(activity: Activity?, onNextAction: () -> Unit) {
        activity?.let { interAll.showAd(activity, true, onNextAction) } ?: run { onNextAction() }
    }
}
