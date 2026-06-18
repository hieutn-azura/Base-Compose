package com.hdt.basecompose.ui.feature

import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import org.koin.android.ext.android.inject

class Feature1Activity : BaseActivity() {

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
        val features = remember { sampleFeatures.map { it.copy(isSelected = false) } }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.feature_that_fits_your_needs)) })
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
                    items(features) { feature ->
                        FeatureItem(
                            feature = feature,
                            isSelected = false,
                            onClick = { navigateToFeature2() }
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
            if (prefs.isUfo()) Analytics.track("ufo_feature_1")
            with(nativeAdsWrapper) { setupNativeAd("native_feature_1"); requestAds() }
        }
    }

    private fun navigateToFeature2() {
        startActivityAndFinish<Feature2Activity>()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finishAffinity() }
        })
    }
}

@Composable
fun FeatureItem(
    feature: FeatureModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(12.dp)

    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isSelected) Modifier.border(1.5.dp, primary, shape)
                else Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), shape)
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                primary.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AsyncImage(
                model = feature.iconId,
                contentDescription = feature.name,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = feature.name,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = 6.dp),
                color = if (isSelected) primary
                        else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
