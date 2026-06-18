package com.hdt.basecompose.ui.language

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.interstitial.InterstitialAdManager
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.remoteconfig.remoteLogic
import com.hdt.basecompose.utils.extensions.moveItemToPosition
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LanguageLoadingActivity : BaseActivity() {

    private val prefs: PreferenceData by inject()
    private var job: Job? = null
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null
    private val loadingProgress = mutableFloatStateOf(0f)

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.LANGUAGE_LOADING,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    override fun isApplyLanguage() = false

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val progress by loadingProgress
        val languages = remember { buildLanguageList() }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.language)) })
            }
        ) { padding ->
            Column(Modifier.fillMaxSize()) {
                // Progress bar + label (below topbar)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding())
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    )
                    Text(
                        text = stringResource(R.string.loading_language_data),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                    )
                }

                LanguageListContent(
                    languages = languages,
                    selectedCode = "",
                    enabled = false,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onItemClick = {},
                )

                NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                    adContainerRef = container
                    shimmerRef = shimmer
                }
            }
        }

        val dummy = remember { setupAds() }
    }

    private fun setupAds(): Boolean {
        lifecycleScope.launch {
            delay(16)
            if (prefs.isUfo()) Analytics.track("ufo_language_loading")
            with(nativeAdsWrapper) {
                setupNativeAd("native_language_loading")
                requestAds()
            }
        }
        return true
    }

    private fun buildLanguageList(): List<LanguageItem> {
        val deviceLanguage = Language.cachedDeviceLanguage
        val list = Language.listLanguage.toMutableList()
        val idx = list.indexOfFirst { it.code == deviceLanguage }
        return if (idx != -1) {
            list[idx].isDefault = true
            list.moveItemToPosition(3) { it.code == deviceLanguage }
        } else {
            list[0].isDefault = true
            list
        }
    }

    private fun countDownSkip() {
        job?.cancel()
        job = lifecycleScope.launch {
            val totalDuration = remoteLogic.languageLoadingConfig.showTimeMs
            val maxProgress = 100
            for (i in 0..maxProgress) {
                if (!isActive) return@launch
                loadingProgress.floatValue = i.toFloat() / maxProgress
                delay(totalDuration / maxProgress)
            }
            startActivityAndFinish<Language1Activity>()
            overridePendingTransition(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        InterstitialAdManager.isCloseInterSplash.observe(this) { if (it) countDownSkip() }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }
}
