package com.hdt.basecompose.ui.feature

import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.MainActivity
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.ui.theme.ColorLabelSecondary
import com.hdt.basecompose.ui.theme.ColorTextDisabled
import org.koin.android.ext.android.inject

class Feature2Activity : BaseActivity() {

    companion object {
        const val EXTRA_SELECTED = "extra_selected"
    }

    private val prefs: PreferenceData by inject()
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.FEATURE_DUP,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val selected = intent.getBooleanArrayExtra(EXTRA_SELECTED)
        val initialFeatures = remember {
            sampleFeatures.mapIndexed { index, feature ->
                feature.copy(isSelected = selected?.getOrNull(index) ?: false)
            }
        }
        var items by remember { mutableStateOf(initialFeatures) }
        val selectedCount = items.count { it.isSelected }
        val primary = MaterialTheme.colorScheme.primary

        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.feature_genre_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_genre_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorLabelSecondary,
                )
                Spacer(Modifier.height(20.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items.forEachIndexed { index, feature ->
                        GenreChip(
                            label = stringResource(feature.nameRes),
                            isSelected = feature.isSelected,
                            onClick = {
                                items = items.toMutableList().also {
                                    it[index] = it[index].copy(isSelected = !it[index].isSelected)
                                }
                            }
                        )
                    }
                }
            }
            TextButton(
                onClick = { if (selectedCount > 2) finishFlow() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    text = stringResource(R.string.text_continue),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selectedCount > 2) primary else ColorTextDisabled,
                )
            }
            NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                adContainerRef = container
                shimmerRef = shimmer
            }
        }

        LaunchedEffect(Unit) {
            if (prefs.isUfo()) Analytics.track("ufo_feature_2")
            with(nativeAdsWrapper) { setupNativeAd("native_feature_2"); requestAds() }
        }
    }

    private fun finishFlow() {
        prefs.isFinishFirstFlow = true
        startActivityAndFinish<MainActivity>()
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finishAffinity() }
        })
    }
}
