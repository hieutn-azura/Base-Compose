package com.hdt.basecompose.style

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Screen queries ─────────────────────────────────────────────────────────────

@Composable
fun getScreenWidthInDp(): Dp {
    val context = LocalContext.current
    return (context.resources.displayMetrics.widthPixels /
            context.resources.displayMetrics.density).dp
}

@Composable
fun getScreenHeightInDp(): Dp {
    val context = LocalContext.current
    return (context.resources.displayMetrics.heightPixels /
            context.resources.displayMetrics.density).dp
}

// ── Spacer helpers ─────────────────────────────────────────────────────────────

/** Square spacer. */
@Composable
fun Space(size: Dp) = Spacer(modifier = Modifier.height(size).width(size))

/** Vertical spacer. */
@Composable
fun SpaceH(size: Dp) = Spacer(modifier = Modifier.height(size))

/** Horizontal spacer. */
@Composable
fun SpaceW(size: Dp) = Spacer(modifier = Modifier.width(size))

/** Fills remaining space in a Row / Column. */
@Composable
fun SpaceF() = Spacer(modifier = Modifier.fillMaxWidth().weight(1f, fill = true))

// Can't add weight extension here without a RowScope/ColumnScope receiver — use SpaceF inside
// a Row/Column by calling Spacer(Modifier.weight(1f)) directly.

// ── Modifier extensions ────────────────────────────────────────────────────────

/** Sets width as a percentage of screen width. */
@Composable
fun Modifier.widthPercent(fraction: Float): Modifier =
    this.width(getScreenWidthInDp() * fraction.coerceIn(0f, 1f))

/** Sets height as a percentage of screen height. */
@Composable
fun Modifier.heightPercent(fraction: Float): Modifier =
    this.height(getScreenHeightInDp() * fraction.coerceIn(0f, 1f))

// ── Conversion ─────────────────────────────────────────────────────────────────

@Composable
fun Int.pxToDp(): Dp {
    val density = LocalDensity.current.density
    return (this / density).dp
}

@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current.density
    return this.value * density
}
