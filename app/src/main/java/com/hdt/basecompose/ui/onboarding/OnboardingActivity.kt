package com.hdt.basecompose.ui.onboarding

import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ads.control.billing.AppPurchase
import com.hdt.basecompose.widget.NativeAdSlot
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdPreloadManager
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.remoteconfig.remoteAds
import com.hdt.basecompose.ui.feature.Feature1Activity
import com.hdt.basecompose.utils.extensions.isInternetAvailable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingActivity : BaseActivity() {

    private val prefs: PreferenceData by inject()
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.ONBOARDING,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    private fun isShowFull1() =
        remoteAds.adNativeOnboardingFull1Config.enable && isInternetAvailable() && !AppPurchase.getInstance().isPurchased

    private fun isShowFull2() =
        remoteAds.adNativeOnboardingFull2Config.enable && isInternetAvailable() && !AppPurchase.getInstance().isPurchased

    @Composable
    override fun Content() {
        val titles = listOf(
            stringResource(R.string.title_onboarding_1),
            stringResource(R.string.title_onboarding_2),
            stringResource(R.string.title_onboarding_3),
            stringResource(R.string.title_onboarding_4),
        )
        val pageColors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.surfaceVariant,
        )
        val pages = remember {
            buildList {
                add(OnboardingPageData(titles[0], pageColors[0], isRegular = true))
                add(OnboardingPageData(titles[1], pageColors[1], isRegular = true))
                if (isShowFull1()) add(OnboardingPageData("", Color.Black, isRegular = false))
                add(OnboardingPageData(titles[2], pageColors[2], isRegular = true))
                if (isShowFull2()) add(OnboardingPageData("", Color.Black, isRegular = false))
                add(OnboardingPageData(titles[3], pageColors[3], isRegular = true))
            }
        }
        val regularCount = pages.count { it.isRegular }
        val pagerState = rememberPagerState(pageCount = { pages.size })
        val scope = rememberCoroutineScope()
        val primary = MaterialTheme.colorScheme.primary

        fun goNext() {
            if (pagerState.currentPage >= pages.size - 1) {
                if (!prefs.isFinishFirstFlow) startActivityAndFinish<Feature1Activity>()
                else startActivityAndFinish<com.hdt.basecompose.MainActivity>()
            } else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        }

        BackHandler {
            if (pagerState.currentPage == 0) finish()
            else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        }

        Column(Modifier.fillMaxSize()) {

            // ── Pager ─────────────────────────────────────────────────────
            Box(Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = true,
                ) { index ->
                    val page = pages[index]
                    Box(
                        Modifier.fillMaxSize().background(page.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!page.isRegular) {
                            // Full-screen native ad page
                            var fullAdRef: FrameLayout? = null
                            AndroidView(
                                factory = { ctx -> FrameLayout(ctx).also { fullAdRef = it } },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Add onboarding illustrations here per page
                    }
                }

                // Bottom overlay: title + indicator + Next (regular pages only)
                val currentPage = pages.getOrNull(pagerState.currentPage)
                if (currentPage?.isRegular == true) {
                    val indicatorIdx = pages
                        .take(pagerState.currentPage + 1)
                        .count { it.isRegular } - 1

                    Column(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = currentPage.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            ),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Indicator dots
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(regularCount) { idx ->
                                    Box(
                                        Modifier
                                            .size(
                                                width = if (idx == indicatorIdx) 20.dp else 7.dp,
                                                height = 7.dp
                                            )
                                            .clip(CircleShape)
                                            .background(
                                                if (idx == indicatorIdx) primary
                                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                    )
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { goNext() }) {
                                Text(
                                    text = if (pagerState.currentPage >= pages.size - 1)
                                        stringResource(R.string.get_started)
                                    else stringResource(R.string.next),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
            }

            // ── Native ad fixed at bottom ──────────────────────────────────
            NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                adContainerRef = container
                shimmerRef = shimmer
            }
        }

        LaunchedEffect(Unit) {
            with(nativeAdsWrapper) {
                setupNativeAd("native_onboarding")
                requestAds()
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            val page = pages.getOrNull(pagerState.currentPage)
            if (page?.isRegular == true) nativeAdsWrapper.requestAdsOnboarding()
            else nativeAdsWrapper.cancelRequest()
            if (pagerState.currentPage == pages.size - 1) {
                NativeAdPreloadManager.preloadAd(this@OnboardingActivity, NativePlacement.FEATURE)
                NativeAdPreloadManager.preloadAd(this@OnboardingActivity, NativePlacement.FEATURE_DUP)
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val bgColor: Color,
    val isRegular: Boolean,
)
