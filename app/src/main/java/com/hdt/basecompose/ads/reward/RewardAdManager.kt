package com.hdt.basecompose.ads.reward

import android.app.Activity

object RewardAdManager {
    private var rewardAll = RewardAdsWrapper(RewardPlacement.REWARD_ALL)

    fun loadRewardAll(activity: Activity) { rewardAll.preload(activity) }

    fun showRewardAll(activity: Activity, onNextAction: () -> Unit, onAdNotReady: () -> Unit) {
        rewardAll.showAd(activity, onNextAction, onAdNotReady)
    }
}
