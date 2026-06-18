package com.hdt.basecompose.style

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Screen wrapper ─────────────────────────────────────────────────────────────

@Composable
fun ViewParentContent(
    onBack: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    BackHandler(onBack = onBack)
    Box(content = content)
}

// ── Scroll views ───────────────────────────────────────────────────────────────

@Composable
fun HorScrollView(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.horizontalScroll(state)) { content() }
}

@Composable
fun VerScrollView(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.verticalScroll(state)) { content() }
}

// ── Shimmer ────────────────────────────────────────────────────────────────────

fun Modifier.shimmer(
    baseColor: Color = AppColors.ShimmerBase,
    highlightColor: Color = AppColors.ShimmerHighlight,
    durationMs: Int = 1200
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(translateAnim - 200f, 0f),
            end   = Offset(translateAnim, 0f),
        )
    )
}

// ── Dashed border ──────────────────────────────────────────────────────────────

fun Modifier.dashedBorder(
    width: Dp = 1.dp,
    color: Color,
    on: Dp = 8.dp,
    off: Dp = 4.dp,
    cornerRadius: Dp = 0.dp
): Modifier = drawWithContent {
    drawContent()
    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(on.toPx(), off.toPx())
            )
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
    )
}

// ── Gradient ───────────────────────────────────────────────────────────────────

fun Modifier.gradientBackground(
    colors: List<Color> = listOf(GradientStart, GradientEnd),
    isVertical: Boolean = true
): Modifier = background(
    brush = if (isVertical)
        Brush.verticalGradient(colors)
    else
        Brush.horizontalGradient(colors)
)

// ── Dot indicator ─────────────────────────────────────────────────────────────

@Composable
fun DotIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Gray300,
    dotSize: Dp = 8.dp,
    spacing: Dp = 6.dp,
) {
    Row(modifier = modifier.wrapContentSize()) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .then(if (i < count - 1) Modifier.wrapContentSize() else Modifier)
                    .background(
                        color = if (i == current) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
            if (i < count - 1) SpaceW(spacing)
        }
    }
}

// ── Directional border modifiers ───────────────────────────────────────────────

fun Modifier.topBorder(width: Dp = 1.dp, color: Color) = drawWithContent {
    drawContent()
    drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), width.toPx())
}

fun Modifier.bottomBorder(width: Dp = 1.dp, color: Color) = drawWithContent {
    drawContent()
    drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), width.toPx())
}

fun Modifier.startBorder(width: Dp = 1.dp, color: Color) = drawWithContent {
    drawContent()
    drawLine(color, Offset(0f, 0f), Offset(0f, size.height), width.toPx())
}

fun Modifier.endBorder(width: Dp = 1.dp, color: Color) = drawWithContent {
    drawContent()
    drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), width.toPx())
}
