package com.hdt.basecompose.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Scalable density-independent pixel — backed by the Intuit SDP library.
 * Usage: `16.sdp()` in any composable instead of `16.dp`.
 */
@Composable
fun Int.sdp(): Dp {
    val id = LocalContext.current.resources
        .getIdentifier("_${this}sdp", "dimen", "com.intuit.sdp")
    return if (id != 0) dimensionResource(id) else this.dp
}

/**
 * Scalable sp for font sizes — backed by the Intuit SSP library.
 * Usage: `14.ssp()` in any composable instead of `14.sp`.
 */
@Composable
fun Int.ssp(): TextUnit {
    val id = LocalContext.current.resources
        .getIdentifier("_${this}ssp", "dimen", "com.intuit.ssp")
    return if (id != 0) dimensionResource(id).value.sp else this.sp
}
