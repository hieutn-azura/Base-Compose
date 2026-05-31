package com.hdt.basecompose.style

import androidx.compose.ui.graphics.Color

/**
 * Project-specific semantic color aliases.
 * Reference these in theme definitions and composables instead of raw hex values.
 * Change here to re-skin the whole app.
 */
object AppColors {
    // Light scheme
    val Primary             = Blue600
    val PrimaryContainer    = Blue100
    val Secondary           = Purple600
    val Background          = Gray50
    val Surface             = White
    val OnBackground        = Gray900
    val OnSurface           = Gray800
    val Error               = Red600
    val Success             = Green600
    val Warning             = Yellow500
    val Divider             = Gray200
    val TextPrimary         = Gray900
    val TextSecondary       = Gray500
    val TextHint            = Gray400

    // Dark scheme
    val PrimaryDark             = Blue400
    val PrimaryContainerDark    = Blue900
    val SecondaryDark           = Purple500
    val BackgroundDark          = Gray900
    val SurfaceDark             = Gray800
    val OnBackgroundDark        = Gray50
    val OnSurfaceDark           = Gray100

    // Shimmer
    val ShimmerBase      = Gray200
    val ShimmerHighlight = Gray100
}
