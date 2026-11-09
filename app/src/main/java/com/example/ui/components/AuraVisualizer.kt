package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.voice.VoiceState
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraCyberViolet
import com.example.ui.theme.AuraElectricBlue
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuraVisualizer(
    modifier: Modifier = Modifier,
    visualizerSize: Dp = 220.dp,
    voiceState: VoiceState = VoiceState.IDLE,
    audioLevel: Float = 0.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_visualizer")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val counterRotationAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val primaryColor = when (voiceState) {
        VoiceState.LISTENING -> AuraCyan
        VoiceState.SPEAKING -> AuraElectricBlue
        VoiceState.PROCESSING -> AuraCyberViolet
        VoiceState.ERROR -> Color(0xFFFF3366)
        VoiceState.IDLE -> AuraCyan.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier.size(visualizerSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) / 2f - 12.dp.toPx()
            val effectiveLevel = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
                (audioLevel * 1.5f).coerceIn(0.1f, 1.0f)
            } else {
                0.2f
            }

            // 1. Ambient outer glow circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.25f * effectiveLevel), Color.Transparent),
                    center = center,
                    radius = maxRadius * 1.2f
                ),
                radius = maxRadius,
                center = center
            )

            // 2. Outer segmented HUD ring (rotating)
            val ringRadius = maxRadius * 0.95f
            val numTicks = 24
            for (i in 0 until numTicks) {
                val tickAngle = (i * (360f / numTicks) + rotationAngle) * (Math.PI / 180f)
                val startDist = ringRadius - 6.dp.toPx()
                val endDist = ringRadius + 2.dp.toPx()
                val startOffset = Offset(
                    (center.x + startDist * cos(tickAngle)).toFloat(),
                    (center.y + startDist * sin(tickAngle)).toFloat()
                )
                val endOffset = Offset(
                    (center.x + endDist * cos(tickAngle)).toFloat(),
                    (center.y + endDist * sin(tickAngle)).toFloat()
                )
                val tickAlpha = if (i % 4 == 0) 0.8f else 0.35f
                drawLine(
                    color = primaryColor.copy(alpha = tickAlpha),
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = if (i % 4 == 0) 2.5.dp.toPx() else 1.2.dp.toPx()
                )
            }

            // 3. Dynamic expanding wave rings
            val wave1Radius = (maxRadius * 0.72f * breathingScale + effectiveLevel * 20.dp.toPx()).coerceAtMost(maxRadius)
            drawCircle(
                color = primaryColor.copy(alpha = 0.45f),
                radius = wave1Radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            val wave2Radius = (maxRadius * 0.52f + effectiveLevel * 30.dp.toPx())
            drawCircle(
                color = AuraElectricBlue.copy(alpha = 0.55f),
                radius = wave2Radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Inner orbital arc segments (counter-rotating)
            val arcRadius = maxRadius * 0.38f
            drawArc(
                color = primaryColor,
                startAngle = counterRotationAngle,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 3.dp.toPx())
            )
            drawArc(
                color = AuraCyberViolet,
                startAngle = counterRotationAngle + 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 3.dp.toPx())
            )

            // 5. Core pulsating luminous sphere
            val coreRadius = maxRadius * 0.22f * (if (voiceState == VoiceState.LISTENING) (1f + effectiveLevel * 0.4f) else breathingScale)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        primaryColor.copy(alpha = 0.15f)
                    ),
                    center = center,
                    radius = coreRadius
                ),
                radius = coreRadius,
                center = center
            )
        }
    }
}
