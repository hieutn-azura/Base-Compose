package com.hdt.basecompose.ui.splash

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.compose.foundation.background
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.R
import androidx.lifecycle.lifecycleScope
import com.ads.control.admob.AdsConsentManager2
import com.ads.control.admob.AppOpenManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.BuildConfig
import com.hdt.basecompose.MainActivity
import com.hdt.basecompose.ads.interstitial.InterstitialAdManager
import com.hdt.basecompose.ads.native.NativeAdPreloadManager
import com.hdt.basecompose.ads.native.NativePlacement
import com.hdt.basecompose.ads.splash.AdSplashCompleteListener
import com.hdt.basecompose.ads.splash.AdSplashManager
import com.hdt.basecompose.ads.splash.AdState
import com.hdt.basecompose.ads.splash.NativeSplashManager
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.remoteconfig.RemoteInitializer
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.remoteconfig.remoteAds
import com.hdt.basecompose.remoteconfig.remoteLogic
import com.hdt.basecompose.ui.language.Language
import com.hdt.basecompose.ui.language.Language1Activity
import com.hdt.basecompose.ui.language.LanguageLoadingActivity
import com.hdt.basecompose.utils.extensions.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(), AdSplashCompleteListener {

    private val prefs: PreferenceData by inject()

    private var isNextAction = false
    private var isAcceptUmp = true
    private var isFirstResume = true
    private var nativeSplashManager: NativeSplashManager? = null
    private var isFinishAdSplash = false
    private var isFinishAdNative = false
    private var jobAdSplash: Job? = null
    private var jobNativeSplash: Job? = null

    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    override fun isApplyLanguage() = false
    override fun isDisplayCutout() = true

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // ── Icon + App name — centered in the top 70% of the screen ──────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = R.mipmap.ic_launcher_round,
                        contentDescription = null,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name).uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // ── Bottom: ad disclaimer + progress + native ad ─────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.this_action_may_contain_advertising),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.24f),
                )
                Spacer(Modifier.height(12.dp))
                NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                    adContainerRef = container
                    shimmerRef = shimmer
                }
            }
        }

        LaunchedEffect(Unit) {
            if (prefs.countSessionApp == 0) Analytics.track("ufo_splash")
            prefs.countSessionApp += 1
            awaitAll(
                async { requestUmp() },
                async { withTimeoutOrNull(30000) { RemoteInitializer.setupRemoteConfig(BuildConfig.build_debug) } }
            )
            initAds()
            setupInAppUpdate()
        }
    }

    private fun observeAds() {
        lifecycleScope.launch {
            AdSplashManager.instance.adState.collectLatest {
                when (it) {
                    AdState.Idle -> Unit
                    AdState.NavigateNext -> navigateToNextScreen()
                    AdState.NativeFullScr -> navigateToNativeFullScreen()
                }
            }
        }
    }

    private fun setupAdManager() {
        val container = adContainerRef ?: return
        val shimmer = shimmerRef ?: return
        nativeSplashManager = NativeSplashManager(this, container, shimmer, this)
        AdSplashManager.instance.bind(this@SplashActivity, this)
    }

    private suspend fun requestUmp() {
        val consentManager = AdsConsentManager2(this@SplashActivity)
        consentManager.requestUMP()
        isAcceptUmp = consentManager.getCanRequestAd()
        if (!isAcceptUmp) finish()
    }

    private fun setupInAppUpdate() {
        // Setup in-app update logic here if needed
    }

    private suspend fun initAds() {
        InterstitialAdManager.resetInterAllConfig()
        InterstitialAdManager.isCloseInterSplash.postValue(false)
        if (isEnableAds()) {
            setupOpenAds()
            setupAdManager()
            nativeSplashManager?.loadNative()
            AdSplashManager.instance.loadAds()
            observeAds()
            preloadAdNextScreen()
        } else {
            InterstitialAdManager.isCloseInterSplash.postValue(true)
            delay(3000)
            navigateToNextScreen()
        }
    }

    private fun preloadAdNextScreen() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (!prefs.isFinishFirstFlow) {
                if (remoteLogic.languageLoadingConfig.enableScreen) {
                    NativeAdPreloadManager.preloadAd(this@SplashActivity, NativePlacement.LANGUAGE_LOADING)
                }
                NativeAdPreloadManager.preloadAd(this@SplashActivity, NativePlacement.LANGUAGE_1)
                NativeAdPreloadManager.preloadAd(this@SplashActivity, NativePlacement.LANGUAGE_2)
            }
        }
    }

    private fun setupOpenAds() {
        if (remoteAds.appOpenAdConfig.enable.not()) return
        val listEnableAds = remoteAds.appOpenAdConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        AppOpenManager.getInstance().init(this.application, listEnableAds)
    }

    private fun isEnableAds() = remoteAds.isAdsEnable && isInternetAvailable() && isAcceptUmp

    private fun navigateToNextScreen() {
        if (isNextAction) return
        isNextAction = true
        if (prefs.isFinishFirstFlow) {
            startActivityAndFinish<MainActivity>()
        } else {
            Language.listLanguage.forEach { it.isDefault = false; it.isChoose = false }
            if (remoteLogic.languageLoadingConfig.enableScreen) {
                startActivityAndFinish<LanguageLoadingActivity>()
            } else {
                startActivityAndFinish<Language1Activity>()
            }
        }
    }

    private fun navigateToNativeFullScreen() {
        startActivityAndFinish<NativeSplashActivity>()
    }

    override fun onDestroy() {
        nativeSplashManager?.cleanup()
        super.onDestroy()
    }

    override fun onAdComplete(adName: String) {
        when (adName) {
            AdSplashManager.TAG -> {
                isFinishAdSplash = true
                if (isFinishAdNative) AdSplashManager.instance.showCurrentAd()
                else handleWaitingAdSplash()
            }
            NativeSplashManager.TAG -> handleDelayAdNative()
        }
    }

    private fun handleWaitingAdSplash() {
        jobAdSplash?.cancel()
        jobAdSplash = lifecycleScope.launch {
            delay(remoteLogic.splashTimeout.waitingMs)
            AdSplashManager.instance.showCurrentAd()
            jobNativeSplash?.cancel()
        }
    }

    private fun handleDelayAdNative() {
        jobNativeSplash?.cancel()
        jobNativeSplash = lifecycleScope.launch {
            delay(remoteLogic.splashTimeout.delayMs)
            isFinishAdNative = true
            if (isFinishAdSplash) {
                AdSplashManager.instance.showCurrentAd()
                jobAdSplash?.cancel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) { isFirstResume = false; return }
        if (remoteAds.adSplashConfig.enable && isEnableAds()) {
            if (isFinishAdSplash && isFinishAdNative) {
                AdSplashManager.instance.onCheckShowAdsWhenFail()
            }
        }
    }
}
