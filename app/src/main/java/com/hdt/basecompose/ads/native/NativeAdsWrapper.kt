package com.hdt.basecompose.ads.`native`

import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ads.control.ads.AzAdCallback
import com.ads.control.ads.AzAds
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.helper.AdOptionVisibility
import com.ads.control.helper.adnative.NativeAdConfig
import com.ads.control.helper.adnative.NativeAdHelper
import com.ads.control.helper.adnative.params.AdNativeMediation
import com.ads.control.helper.adnative.params.AdNativeState
import com.ads.control.helper.adnative.params.NativeAdParam
import com.ads.control.helper.adnative.params.NativeLayoutMediation
import com.ads.control.helper.adnative.preload.NativeAdPreload
import com.ads.control.helper.adnative.preload.NativePreloadState
import com.hdt.basecompose.R
import com.hdt.basecompose.remoteconfig.model.type.LayoutNativeType
import com.hdt.basecompose.remoteconfig.remoteAds
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

class NativeAdsWrapper(
    activity: AppCompatActivity,
    private val config: NativePlacement,
    private val lifecycleOwner: LifecycleOwner,
    private val adContainer: () -> FrameLayout,
    private val shimmerView: () -> ShimmerFrameLayout
) : DefaultLifecycleObserver {
    private var activityRef: WeakReference<AppCompatActivity> = WeakReference(activity)

    private val nativeAdHelper: NativeAdHelper by lazy {
        NativeAdHelperFactory.create(
            activity = activity,
            placement = config,
            lifecycleOwner = lifecycleOwner
        )
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
        super.onDestroy(owner)
    }

    private fun cleanup() {
        nativeAdHelper.cancel()
        activityRef.clear()
    }

    fun getAvailableAdCount(): Int = NativeAdPreloadManager.getAvailableAdCount(config)

    fun getAdPreloadState(): StateFlow<NativePreloadState> =
        NativeAdPreloadManager.getAdPreloadState(config)

    fun setupNativeAd(tag: String? = null) {
        nativeAdHelper.apply {
            setNativeContentView(adContainer.invoke())
            setShimmerLayoutView(shimmerView.invoke())
            tag?.let { setTagForDebug(it) }
            activityRef.get()?.let { activity ->
                if (config.displayLayoutId != null) {
                    setCustomContentView { nativeAd ->
                        nativeAd.layoutCustomNative = config.displayLayoutId ?: config.preloadLayoutId()
                        AzAds.getInstance().populateNativeAdView(activity, nativeAd, adContainer(), shimmerView())
                    }
                }
            }
        }
    }

    fun setupNativeAd(tag: String? = null, block: NativeAdView.() -> Unit) {
        nativeAdHelper.apply {
            setNativeContentView(adContainer.invoke())
            setShimmerLayoutView(shimmerView.invoke())
            tag?.let { setTagForDebug(it) }
            activityRef.get()?.let { activity ->
                setCustomContentView { nativeAd ->
                    val layout = config.displayLayoutId ?: config.preloadLayoutId()
                    nativeAd.layoutCustomNative = layout
                    AzAds.getInstance().populateNativeAdView(activity, nativeAd, adContainer(), shimmerView())
                    val adView = adContainer().getChildAt(0) as? NativeAdView
                    adView?.block()
                }
            }
        }
    }

    fun setFlagReload(isReload: Boolean) {
        nativeAdHelper.flagUserEnableReload = isReload
    }

    fun registerAdCallbacks(
        onLoaded: ((ApNativeAd) -> Unit)? = null,
        onFailed: ((ApAdError?) -> Unit)? = null,
        onClicked: (() -> Unit)? = null,
        onImpression: (() -> Unit)? = null
    ) {
        nativeAdHelper.registerAdListener(object : AzAdCallback() {
            override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                super.onNativeAdLoaded(nativeAd)
                onLoaded?.invoke(nativeAd)
            }
            override fun onAdFailedToLoad(adError: ApAdError?) {
                super.onAdFailedToLoad(adError)
                onFailed?.invoke(adError)
            }
            override fun onAdClicked() {
                super.onAdClicked()
                onClicked?.invoke()
            }
            override fun onAdImpression() {
                super.onAdImpression()
                onImpression?.invoke()
            }
        })
    }

    fun getAdState(): Flow<AdNativeState> = nativeAdHelper.getAdNativeState()

    fun cancelRequest() {
        nativeAdHelper.cancel()
    }

    fun requestAds() = nativeAdHelper.requestAds(NativeAdParam.Request.create())

    fun requestAdsFragment() {
        nativeAdHelper.nativeAd?.let { nativeAdHelper.requestAds(NativeAdParam.Ready(it)) }
            ?: run { nativeAdHelper.requestAds(NativeAdParam.Request.create()) }
    }

    fun requestAdsOnboarding() {
        nativeAdHelper.nativeAd?.let { nativeAdHelper.requestAds(NativeAdParam.Ready(it)) }
        nativeAdHelper.requestAds(NativeAdParam.Request.create())
    }

    fun pollAdNative(): ApNativeAd? {
        if (!config.canShowAds()) return null
        return NativeAdPreload.getInstance().pollAdNative(
            listId = config.listId(),
            layoutId = config.preloadLayoutId()
        )
    }

    fun safePreloadAd(buffer: Int = 1) {
        activityRef.get()?.let { activity -> NativeAdPreloadManager.safePreloadAd(activity, config, buffer) }
    }

    fun preloadAd(buffer: Int = 1) {
        activityRef.get()?.let { activity -> NativeAdPreloadManager.preloadAd(activity, config, buffer) }
    }
}

internal object NativeAdHelperFactory {
    fun create(
        activity: AppCompatActivity,
        placement: NativePlacement,
        lifecycleOwner: LifecycleOwner? = null,
    ): NativeAdHelper {
        val canShowAds = placement.canShowAds()
        return NativeAdHelper(
            activity,
            lifecycleOwner ?: activity,
            createNativeAdConfig(placement, canShowAds, placement.preloadLayoutId())
        ).apply {
            setEnableListNative(true)
            setEnablePreload(true)
            adVisibility = placement.adVisibility
        }
    }

    private fun createNativeAdConfig(
        placement: NativePlacement,
        canShowAds: Boolean,
        finalLayoutId: Int,
    ): NativeAdConfig {
        return NativeAdConfig(
            idAds = placement.listId().lastOrNull() ?: "",
            canShowAds = canShowAds,
            canReloadAds = true,
            layoutId = finalLayoutId,
        ).apply {
            setListId(placement.listId())
            if (placement.layoutMeta != null) {
                setLayoutMediation(NativeLayoutMediation(AdNativeMediation.FACEBOOK, placement.layoutMeta.invoke()))
            }
        }
    }
}

enum class NativePlacement(
    val listId: () -> List<String>,
    val canShowAds: () -> Boolean,
    val adVisibility: AdOptionVisibility = AdOptionVisibility.GONE,
    @LayoutRes val preloadLayoutId: () -> Int,
    @LayoutRes var displayLayoutId: Int? = null,
    val layoutMeta: (() -> Int)? = null,
) {
    SPLASH(
        listId = { remoteAds.adNativeSplashConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeSplashConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeSplashConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeSplashConfig.layout) },
    ),
    LANGUAGE_LOADING(
        listId = { remoteAds.adNativeLanguageLoadingConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeLanguageLoadingConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeLanguageLoadingConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeLanguageLoadingConfig.layout) },
    ),
    LANGUAGE_1(
        listId = { remoteAds.adNativeLanguageConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeLanguageConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeLanguageConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeLanguageConfig.layout) },
    ),
    LANGUAGE_2(
        listId = { remoteAds.adNativeLanguageDupConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeLanguageDupConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeLanguageDupConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeLanguageDupConfig.layout) },
    ),
    ONBOARDING(
        listId = { remoteAds.adNativeOnboardingConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeOnboardingConfig.enable },
        adVisibility = AdOptionVisibility.INVISIBLE,
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeOnboardingConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeOnboardingConfig.layout) },
    ),
    ONBOARDING_FULL_1(
        listId = { remoteAds.adNativeOnboardingFull1Config.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeOnboardingFull1Config.enable },
        preloadLayoutId = { R.layout.layout_native_full_screen_onb },
        layoutMeta = { LayoutSelector.getMetaLayout(isFullScreen = true) },
    ),
    ONBOARDING_FULL_2(
        listId = { remoteAds.adNativeOnboardingFull2Config.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeOnboardingFull2Config.enable },
        preloadLayoutId = { R.layout.layout_native_full_screen_onb },
        layoutMeta = { LayoutSelector.getMetaLayout(isFullScreen = true) },
    ),
    FEATURE(
        listId = { remoteAds.adNativeFeatureConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeFeatureConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeFeatureConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeFeatureConfig.layout) },
    ),
    FEATURE_DUP(
        listId = { remoteAds.adNativeFeatureDupConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeFeatureDupConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeFeatureDupConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeFeatureDupConfig.layout) },
    ),
    HOME(
        listId = { remoteAds.adNativeHomeConfig.listAds.filter { it.enableAd }.map { it.adUnit } },
        canShowAds = { remoteAds.adNativeHomeConfig.enable },
        preloadLayoutId = { LayoutSelector.getLayout(remoteAds.adNativeHomeConfig.layout) },
        layoutMeta = { LayoutSelector.getMetaLayout(type = remoteAds.adNativeHomeConfig.layout) },
    ),
    ;

    private object LayoutSelector {
        fun getMetaLayout(isFullScreen: Boolean = false, type: String = ""): Int {
            val metaCtrLow = remoteAds.metaCtrLow
            if (isFullScreen) {
                return if (metaCtrLow) R.layout.layout_native_full_screen_meta_low
                else R.layout.layout_native_full_screen_meta_high
            }
            return when (type) {
                LayoutNativeType.NativeSmallCtaBottom.type -> if (metaCtrLow) R.layout.layout_native_small_meta_low_cta_bottom else R.layout.layout_native_small_meta_high_cta_bottom
                LayoutNativeType.NativeSmallCtaTop.type -> if (metaCtrLow) R.layout.layout_native_small_meta_low_cta_top else R.layout.layout_native_small_meta_high_cta_top
                LayoutNativeType.NativeSmallCtaRight.type -> R.layout.layout_native_small_cta_right
                LayoutNativeType.NativeMediumCtaBottom.type -> if (metaCtrLow) R.layout.layout_native_medium_meta_low_cta_bottom else R.layout.layout_native_medium_meta_high_cta_bottom
                LayoutNativeType.NativeMediumCtaTop.type -> if (metaCtrLow) R.layout.layout_native_medium_meta_low_cta_top else R.layout.layout_native_medium_meta_high_cta_top
                LayoutNativeType.MediumCtaRightBottom.type -> R.layout.layout_native_medium_cta_right_bottom
                LayoutNativeType.MediumCtaRightTop.type -> R.layout.layout_native_medium_cta_right_top
                LayoutNativeType.NativeMediumMediaLeftCtaBottom.type -> if (metaCtrLow) R.layout.layout_native_medium_meta_low_left_cta_bottom else R.layout.layout_native_medium_meta_high_left_cta_bottom
                LayoutNativeType.NativeMediumMediaLeftCtaRight.type -> if (metaCtrLow) R.layout.layout_native_medium_meta_low_left_cta_right else R.layout.layout_native_medium_meta_high_left_cta_right
                else -> if (metaCtrLow) R.layout.layout_native_medium_meta_low_left_cta_bottom else R.layout.layout_native_medium_meta_high_left_cta_bottom
            }
        }

        fun getLayout(type: String): Int {
            return when (type) {
                LayoutNativeType.NativeSmallCtaBottom.type -> R.layout.layout_native_small_cta_bottom
                LayoutNativeType.NativeSmallCtaTop.type -> R.layout.layout_native_small_cta_top
                LayoutNativeType.NativeSmallCtaRight.type -> R.layout.layout_native_small_cta_right
                LayoutNativeType.NativeMediumCtaBottom.type -> R.layout.layout_native_medium_cta_bottom
                LayoutNativeType.NativeMediumCtaTop.type -> R.layout.layout_native_medium_cta_top
                LayoutNativeType.MediumCtaRightBottom.type -> R.layout.layout_native_medium_cta_right_bottom
                LayoutNativeType.MediumCtaRightTop.type -> R.layout.layout_native_medium_cta_right_top
                LayoutNativeType.NativeMediumMediaLeftCtaBottom.type -> R.layout.layout_native_medium_left_cta_bottom
                LayoutNativeType.NativeMediumMediaLeftCtaRight.type -> R.layout.layout_native_medium_left_cta_right
                else -> R.layout.layout_native_medium_left_cta_bottom
            }
        }
    }
}
