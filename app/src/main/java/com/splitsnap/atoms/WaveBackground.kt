package com.splitsnap.atoms

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun WaveGradient(
    modifier: Modifier=Modifier,
    colorList: List<Color> = listOf(
        Color(0xff7057f5).copy(0.4f),
        Color(0xff86f7fa).copy(alpha = 0.4f)),
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        tween(50000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset = with(LocalDensity.current) {
        5000.dp.toPx()
    }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = targetOffset,
        animationSpec = animationSpec,
        label = "offset"
    )

    Box(
        modifier
            .fillMaxSize()
            .blur(50.dp)
            .drawWithCache {
                val brushSize = 400f
                val brush = Brush.linearGradient(
                    colors = colorList,
                    start = Offset(offset, offset),
                    end = Offset(offset + brushSize, offset + brushSize),
                    tileMode = TileMode.Mirror
                )
                onDrawBehind {
                    drawRect(brush)
                }

            })
}
