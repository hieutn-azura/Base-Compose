package com.hdt.basecompose.ui.language

import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import com.facebook.shimmer.ShimmerFrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdPreloadManager
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.remoteconfig.remoteAds
import com.hdt.basecompose.utils.extensions.moveItemToPosition
import org.koin.android.ext.android.inject

class Language1Activity : BaseActivity() {

    private val prefs: PreferenceData by inject()
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.LANGUAGE_1,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    override fun isApplyLanguage() = false

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val listState = rememberLazyListState()
        val languages = remember { buildLanguageList() }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.language)) })
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .run { this }
            ) {
                LanguageListContent(
                    languages = languages,
                    selectedCode = "",
                    enabled = true,
                    listState = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding()),
                    onItemClick = { item ->
                        navigateToLfo2(item, listState.firstVisibleItemScrollOffset)
                    },
                )

                NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                    adContainerRef = container
                    shimmerRef = shimmer
                }
            }
        }

        LaunchedEffect(Unit) {
            if (prefs.isUfo()) Analytics.track("ufo_language")
            if (remoteAds.onboardingScreenConfig.count() > 0) {
                NativeAdPreloadManager.preloadAd(
                    this@Language1Activity,
                    NativePlacement.ONBOARDING,
                    remoteAds.onboardingScreenConfig.count()
                )
            }
            with(nativeAdsWrapper) {
                setupNativeAd("native_language_1")
                requestAds()
            }
        }
    }

    private fun buildLanguageList(): List<LanguageItem> {
        val deviceLanguage = Language.cachedDeviceLanguage
        Language.listLanguage.forEach { it.isDefault = false; it.isChoose = false }
        val idx = Language.listLanguage.indexOfFirst { it.code == deviceLanguage }
        return if (idx != -1) {
            Language.listLanguage[idx].isDefault = true
            Language.listLanguage.moveItemToPosition(3) { it.code == deviceLanguage }
        } else {
            Language.listLanguage[0].isDefault = true
            Language.listLanguage
        }
    }

    private fun navigateToLfo2(item: LanguageItem, scrollOffset: Int) {
        startActivityAndFinish<Language2Activity> {
            putExtra(Language2Activity.EXTRA_SELECTED_CODE, item.code)
            putExtra(Language2Activity.EXTRA_SCROLL_OFFSET, scrollOffset)
        }
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finishAffinity() }
        })
    }
}
