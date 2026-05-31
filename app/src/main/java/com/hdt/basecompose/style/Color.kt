package com.hdt.basecompose.style

import androidx.compose.ui.graphics.Color

// ── Neutrals ──────────────────────────────────────────────────────────────────
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Transparent = Color(0x00000000)

val Gray50  = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D5DB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray600 = Color(0xFF4B5563)
val Gray700 = Color(0xFF374151)
val Gray800 = Color(0xFF1F2937)
val Gray900 = Color(0xFF111827)

// ── Blues ─────────────────────────────────────────────────────────────────────
val Blue50  = Color(0xFFEFF6FF)
val Blue100 = Color(0xFFDBEAFE)
val Blue200 = Color(0xFFBFDBFE)
val Blue300 = Color(0xFF93C5FD)
val Blue400 = Color(0xFF60A5FA)
val Blue500 = Color(0xFF3B82F6)
val Blue600 = Color(0xFF2563EB)
val Blue700 = Color(0xFF1D4ED8)
val Blue800 = Color(0xFF1E40AF)
val Blue900 = Color(0xFF1E3A8A)

// ── Greens ────────────────────────────────────────────────────────────────────
val Green50  = Color(0xFFF0FDF4)
val Green100 = Color(0xFFDCFCE7)
val Green200 = Color(0xFFBBF7D0)
val Green400 = Color(0xFF4ADE80)
val Green500 = Color(0xFF22C55E)
val Green600 = Color(0xFF16A34A)
val Green700 = Color(0xFF15803D)

// ── Reds ──────────────────────────────────────────────────────────────────────
val Red50  = Color(0xFFFFF1F2)
val Red100 = Color(0xFFFFE4E6)
val Red400 = Color(0xFFF87171)
val Red500 = Color(0xFFEF4444)
val Red600 = Color(0xFFDC2626)
val Red700 = Color(0xFFB91C1C)

// ── Yellows / Oranges ─────────────────────────────────────────────────────────
val Yellow400 = Color(0xFFFACC15)
val Yellow500 = Color(0xFFEAB308)
val Orange400 = Color(0xFFFB923C)
val Orange500 = Color(0xFFF97316)

// ── Purples ───────────────────────────────────────────────────────────────────
val Purple50  = Color(0xFFFAF5FF)
val Purple200 = Color(0xFFE9D5FF)
val Purple500 = Color(0xFFA855F7)
val Purple600 = Color(0xFF9333EA)
val Purple700 = Color(0xFF7E22CE)

// ── Gradient helpers ──────────────────────────────────────────────────────────
val GradientStart  = Blue600
val GradientCenter = Blue500
val GradientEnd    = Blue400

// ── Utilities ─────────────────────────────────────────────────────────────────

/** Lightens or darkens by [factor] in range -1..1 (positive = lighter). */
fun Color.adjustLevel(factor: Float): Color {
    val f = factor.coerceIn(-1f, 1f)
    return copy(
        red   = (red   + (if (f > 0) (1f - red)   else red)   * f).coerceIn(0f, 1f),
        green = (green + (if (f > 0) (1f - green) else green) * f).coerceIn(0f, 1f),
        blue  = (blue  + (if (f > 0) (1f - blue)  else blue)  * f).coerceIn(0f, 1f),
    )
}

/** Returns a copy with [alpha] in range 0..1. */
fun Color.opacity(alpha: Float) = copy(alpha = alpha.coerceIn(0f, 1f))
