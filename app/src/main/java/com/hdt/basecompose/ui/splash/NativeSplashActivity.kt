package com.hdt.basecompose.ui.splash

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ads.control.ads.AzAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.helper.adnative.NativeAdConfig
import com.ads.control.helper.adnative.NativeAdHelper
import com.ads.control.helper.adnative.params.AdNativeMediation
import com.ads.control.helper.adnative.params.NativeAdParam
import com.ads.control.helper.adnative.params.NativeLayoutMediation
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.R
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.remoteconfig.model.splash.SplashConfig
import com.hdt.basecompose.remoteconfig.remoteAds
import com.hdt.basecompose.remoteconfig.remoteLogic
import com.hdt.basecompose.ui.language.Language
import com.hdt.basecompose.ui.language.Language1Activity
import com.hdt.basecompose.ui.language.LanguageLoadingActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class NativeSplashActivity : BaseActivity() {
    companion object {
        var nativeAdSplash: SplashConfig? = null
    }

    private val prefs: PreferenceData by inject()
    private var job: Job? = null
    private var jobLoading: Job? = null
    private var isFinishLoading = false
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdHelper: NativeAdHelper? by lazy {
        nativeAdSplash?.let { splash ->
            val config = NativeAdConfig(
                splash.adUnit,
                remoteAds.isAdsEnable,
                true,
                R.layout.layout_native_full_screen_onb,
            ).also { cfg ->
                val layoutMeta = if (remoteAds.metaCtrLow) R.layout.layout_native_full_screen_meta_low
                else R.layout.layout_native_full_screen_meta_high
                cfg.setLayoutMediation(NativeLayoutMediation(AdNativeMediation.FACEBOOK, layoutMeta))
            }
            NativeAdHelper(this, this, config).apply { setEnablePreload(true) }
        }
    }

    override fun isApplyLanguage() = false

    @Composable
    override fun Content() {
        var showLoading by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            AndroidView(
                factory = { ctx -> ShimmerFrameLayout(ctx).also { shimmerRef = it } },
                modifier = Modifier.fillMaxSize()
            )
            AndroidView(
                factory = { ctx -> FrameLayout(ctx).also { adContainerRef = it } },
                modifier = Modifier.fillMaxSize()
            )
            AnimatedVisibility(
                visible = showLoading,
                modifier = Modifier.align(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        }

        LaunchedEffect(Unit) {
            delay(1000)
            showLoading = false
            loadAds()
            countDownSkipInternal()
        }
    }

    private fun loadAds() {
        val container = adContainerRef ?: return
        val shimmer = shimmerRef ?: return
        nativeAdHelper?.apply {
            setNativeContentView(container)
            setShimmerLayoutView(shimmer)
            requestAds(NativeAdParam.Request.create())
            registerAdListener(object : AzAdCallback() {
                override fun onAdImpression() {
                    super.onAdImpression()
                    initListenerOnAd()
                }
                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    navigateToNextScreen()
                }
            })
        }
        initListenerOnAd()
    }

    private fun initListenerOnAd() {
        runCatching {
            adContainerRef?.findViewById<View>(R.id.btnNext)?.setOnClickListener { navigateToNextScreen() }
            val btnSkip = adContainerRef?.findViewById<View>(R.id.tvSkip)
            btnSkip?.postDelayed({
                btnSkip.isVisible = remoteLogic.nativeFullSplashConfig.isShowClose
            }, remoteLogic.nativeFullSplashConfig.delayShowClose)
            btnSkip?.setOnClickListener { navigateToNextScreen() }
        }
    }

    private fun navigateToNextScreen() {
        job?.cancel()
        if (!prefs.isFinishFirstFlow) {
            Language.listLanguage.forEach { it.isDefault = false; it.isChoose = false }
            if (remoteLogic.languageLoadingConfig.enableScreen) {
                startActivityAndFinish<LanguageLoadingActivity>()
            } else {
                startActivityAndFinish<Language1Activity>()
            }
        } else {
            startActivityAndFinish<com.hdt.basecompose.MainActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFinishLoading) return
        countDownSkipInternal()
    }

    private fun countDownSkipInternal() {
        job?.cancel()
        val remoteDelay = remoteLogic.nativeFullSplashConfig.timeDelaySkip
        val delayTime = if (isFinishLoading) (remoteDelay - 1000).coerceAtLeast(0L) else remoteDelay
        isFinishLoading = true
        job = lifecycleScope.launch {
            delay(delayTime)
            if (isActive) navigateToNextScreen()
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }
}
