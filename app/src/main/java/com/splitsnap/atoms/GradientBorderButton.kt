package com.splitsnap.atoms

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.splitsnap.R


@Composable
fun GradientBorderButton(showLoader:Boolean=false,onClick:()->Unit) {
    val targetOffset = with(LocalDensity.current) {
        1000.dp.toPx()
    }
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = targetOffset, animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "offset"
    )
    val brushColors = listOf(
        Color(0xFF4285F4),  // Google Blue
        Color(0xFFEA4335),  // Google Red
        Color(0xFFFBBC05),  // Google Yellow
        Color(0xFF34A853)   // Google Green
    )
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (showLoader) Modifier.fillMaxWidth().border(
                    2.dp, brush = Brush.linearGradient(
                        colors = brushColors,
                        start = Offset(offset, offset),
                        end = Offset(offset + 1000f, offset + 1000f),
                        tileMode = TileMode.Repeated
                    ), shape = RoundedCornerShape(24.dp)
                ) else Modifier
            ),
        enabled = true
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.android_neutral_rd_na), // Replace with your icon resource
                contentDescription = "Create Group", // Replace with your string resource
                modifier = Modifier.size(36.dp)
                    .padding(end = 8.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Sign In with Google ",
                fontStyle = MaterialTheme.typography.labelSmall.fontStyle
            )
        }
    }
}