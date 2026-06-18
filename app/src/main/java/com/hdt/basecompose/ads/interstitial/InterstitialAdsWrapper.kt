package com.hdt.basecompose.ads.interstitial

import android.app.Activity
import android.content.Context
import com.ads.control.ads.AzAdCallback
import com.ads.control.ads.AzAds
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.billing.AppPurchase
import com.hdt.basecompose.remoteconfig.remoteAds
import com.hdt.basecompose.utils.extensions.isInternetAvailable

class InterstitialAdsWrapper(private val config: InterstitialPlacement) {
    private var lastTimeInterstitialShow = 0L
    private var isRequestAds = false
    private var interAd: ApInterstitialAd? = null
    private var currentStepInter = 1
    private val showType by lazy {
        when {
            config.timeInterval() > 0 -> InterShowType.INTERVAL
            else -> InterShowType.NONE
        }
    }

    private fun isAdsReady() = interAd?.isReady == true

    fun resetConfig() {
        lastTimeInterstitialShow = 0
        currentStepInter = 1
    }

    fun updateTimeShowInter() {
        lastTimeInterstitialShow = System.currentTimeMillis()
    }

    fun preloadAd(context: Context) {
        if (!isRequestAds && !isAdsReady() && context.isInternetAvailable()
            && config.canShowAds() && !AppPurchase.getInstance().isPurchased
        ) {
            isRequestAds = true
            AzAds.getInstance().getInterstitialAdsList(context, config.listId(),
                object : AzAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interAd = interstitialAd; isRequestAds = false
                    }
                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError); isRequestAds = false
                    }
                }
            )
        }
    }

    fun showAd(activity: Activity, shouldReloadAds: Boolean = false, onNextAction: () -> Unit = {}) {
        when (showType) {
            InterShowType.INTERVAL -> showInterInterval(activity, shouldReloadAds, onNextAction)
            InterShowType.NONE -> showInterNone(activity, shouldReloadAds, onNextAction)
        }
    }

    private fun showInterInterval(activity: Activity, shouldReloadAds: Boolean, onNextAction: () -> Unit) {
        if (canShowAdsInterval() || canShowAdsStepInterval()) {
            AzAds.getInstance().forceShowInterstitial(activity, interAd,
                object : AzAdCallback() {
                    override fun onNextAction() { super.onNextAction(); onNextAction() }
                    override fun onAdClosed() {
                        super.onAdClosed()
                        lastTimeInterstitialShow = System.currentTimeMillis()
                        interAd = null
                        if (shouldReloadAds) preloadAd(activity)
                    }
                }, false
            )
        } else onNextAction()
        currentStepInter++
    }

    private fun showInterNone(activity: Activity, shouldReloadAds: Boolean, onNextAction: () -> Unit) {
        if (canShowAdsStep()) {
            AzAds.getInstance().forceShowInterstitial(activity, interAd,
                object : AzAdCallback() {
                    override fun onNextAction() { super.onNextAction(); onNextAction() }
                    override fun onAdClosed() {
                        super.onAdClosed()
                        lastTimeInterstitialShow = System.currentTimeMillis()
                        interAd = null
                        if (shouldReloadAds) preloadAd(activity)
                    }
                }, false
            )
        } else onNextAction()
        currentStepInter++
    }

    private fun canShowAdsInterval() = System.currentTimeMillis() - lastTimeInterstitialShow > config.timeInterval() && isAdsReady()
    private fun canShowAdsStepInterval() = config.timeSteps().isNotEmpty() && config.timeSteps().contains(currentStepInter) && isAdsReady()
    private fun canShowAdsStep() = if (config.timeSteps().isEmpty() && isAdsReady()) true else config.timeSteps().contains(currentStepInter) && isAdsReady()
}

enum class InterShowType { INTERVAL, NONE }

enum class InterstitialPlacement(
    val listId: () -> List<String>,
    val canShowAds: () -> Boolean,
    val timeInterval: () -> Long,
    val timeSteps: () -> List<Int>,
) {
    INTER_ALL(
        listId = { remoteAds.adInterAllConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adInterAllConfig.enable },
        timeInterval = { remoteAds.adInterAllConfig.timeInterval },
        timeSteps = { remoteAds.adInterAllConfig.timeSteps },
    ),
}
