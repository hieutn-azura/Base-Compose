package com.hdt.basecompose.widget

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.shimmer.ShimmerFrameLayout

/**
 * Wraps shimmer + adContainer in a single FrameLayout so the Android View system
 * can correctly WRAP_CONTENT the combined height. Starts at 0dp (both children GONE),
 * grows when the NativeAdsWrapper makes shimmer or ad visible.
 *
 * Using two separate AndroidViews with matchParentSize() inside a Box without explicit
 * height results in 0dp — this composable fixes that.
 */
@Composable
fun NativeAdSlot(
    modifier: Modifier = Modifier,
    onViewsReady: (adContainer: FrameLayout, shimmer: ShimmerFrameLayout) -> Unit,
) {
    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).also { wrapper ->
                val shimmer = ShimmerFrameLayout(ctx)
                val adContainer = FrameLayout(ctx)
                wrapper.addView(
                    shimmer,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                    )
                )
                wrapper.addView(
                    adContainer,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                    )
                )
                onViewsReady(adContainer, shimmer)
            }
        },
        modifier = modifier,
    )
}
