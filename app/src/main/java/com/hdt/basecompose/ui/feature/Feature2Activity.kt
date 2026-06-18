package com.hdt.basecompose.ui.feature

import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.MainActivity
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import org.koin.android.ext.android.inject

class Feature2Activity : BaseActivity() {

    private val prefs: PreferenceData by inject()
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.FEATURE,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var features by remember { mutableStateOf(sampleFeatures.map { it.copy() }) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.feature_that_fits_your_needs)) },
                    actions = {
                        IconButton(onClick = { finishFlow() }) {
                            Icon(Icons.Default.Check, contentDescription = stringResource(R.string.done))
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(features) { index, feature ->
                        FeatureItem(
                            feature = feature,
                            isSelected = feature.isSelected,
                            onClick = {
                                features = features.toMutableList().also {
                                    it[index] = it[index].copy(isSelected = !it[index].isSelected)
                                }
                            }
                        )
                    }
                }
                NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                    adContainerRef = container
                    shimmerRef = shimmer
                }
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
