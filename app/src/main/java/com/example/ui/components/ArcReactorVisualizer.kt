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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.voice.VoiceState
import com.example.ui.theme.AuraAmber
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraElectricBlue
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorVisualizer(
    modifier: Modifier = Modifier,
    reactorSize: Dp = 260.dp,
    voiceState: VoiceState = VoiceState.IDLE,
    audioLevel: Float = 0.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arc_reactor")

    val rotorAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotor_rotation"
    )

    val fluxPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flux_pulse"
    )

    val coreGlowColor = when (voiceState) {
        VoiceState.LISTENING -> AuraCyan
        VoiceState.SPEAKING -> AuraElectricBlue
        VoiceState.PROCESSING -> AuraAmber
        VoiceState.ERROR -> Color(0xFFFF3366)
        VoiceState.IDLE -> AuraCyan
    }

    Box(
        modifier = modifier.size(reactorSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) / 2f - 14.dp.toPx()
            val effectiveLevel = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
                (audioLevel * 1.6f).coerceIn(0.15f, 1f)
            } else {
                0.25f
            }

            // 1. Ambient Arc Reactor Aura (outer glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreGlowColor.copy(alpha = 0.35f * fluxPulse),
                        AuraElectricBlue.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.25f
                ),
                radius = maxRadius * 1.15f,
                center = center
            )

            // 2. Heavy Outer Stator Ring (Dark Titanium with Cyan Edge)
            drawCircle(
                color = Color(0xFF14243B),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 8.dp.toPx())
            )
            drawCircle(
                color = AuraCyan.copy(alpha = 0.6f),
                radius = maxRadius + 3.dp.toPx(),
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3. The 10 Iconic Arc Reactor Magnetic Coils (Coil Pods)
            val numCoils = 10
            val coilRadius = maxRadius * 0.85f
            for (i in 0 until numCoils) {
                val angle = (i * (360f / numCoils) + (rotorAngle * 0.4f)) * (Math.PI / 180f)
                val coilCenter = Offset(
                    (center.x + coilRadius * cos(angle)).toFloat(),
                    (center.y + coilRadius * sin(angle)).toFloat()
                )

                // Draw copper/cyan magnetic coil pod
                drawCircle(
                    color = Color(0xFF0A1526),
                    radius = 9.dp.toPx(),
                    center = coilCenter
                )
                drawCircle(
                    color = coreGlowColor.copy(alpha = 0.9f),
                    radius = 6.dp.toPx() * (if (i % 2 == 0) fluxPulse else 1f),
                    center = coilCenter
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = 2.5.dp.toPx(),
                    center = coilCenter
                )
            }

            // 4. Middle Revolving Energy Field
            val midRadius = maxRadius * 0.65f
            drawCircle(
                color = AuraCyan.copy(alpha = 0.4f),
                radius = midRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Dynamic segmented ticks inside middle ring
            val numTicks = 30
            for (j in 0 until numTicks) {
                val tickAngle = (j * (360f / numTicks) - rotorAngle) * (Math.PI / 180f)
                val start = midRadius - 4.dp.toPx()
                val end = midRadius + 4.dp.toPx()
                drawLine(
                    color = if (j % 3 == 0) AuraCyan else AuraCyan.copy(alpha = 0.3f),
                    start = Offset((center.x + start * cos(tickAngle)).toFloat(), (center.y + start * sin(tickAngle)).toFloat()),
                    end = Offset((center.x + end * cos(tickAngle)).toFloat(), (center.y + end * sin(tickAngle)).toFloat()),
                    strokeWidth = if (j % 3 == 0) 2.dp.toPx() else 1.dp.toPx()
                )
            }

            // 5. Triangular Magnetic Core Ring (Stark Mark VI/VII Icon)
            val triRadius = maxRadius * 0.42f * fluxPulse
            val path = Path()
            for (k in 0..2) {
                val triAngle = (k * 120f - 90f + rotorAngle) * (Math.PI / 180f)
                val pt = Offset(
                    (center.x + triRadius * cos(triAngle)).toFloat(),
                    (center.y + triRadius * sin(triAngle)).toFloat()
                )
                if (k == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
            }
            path.close()

            drawPath(
                path = path,
                color = AuraCyan.copy(alpha = 0.7f),
                style = Stroke(width = 3.dp.toPx())
            )

            // 6. Central High-Intensity Vibranium Plasma Core
            val coreRadius = maxRadius * 0.22f * (1f + effectiveLevel * 0.35f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        coreGlowColor,
                        coreGlowColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreRadius * 1.2f
                ),
                radius = coreRadius,
                center = center
            )
        }
    }
}
