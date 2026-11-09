package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraBorderGlowing
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraSurfaceElevated

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    glowing: Boolean = false,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (glowing) {
        Brush.linearGradient(
            listOf(AuraCyan, AuraElectricBlue, AuraBorderGlowing)
        )
    } else {
        Brush.linearGradient(
            listOf(AuraBorderGlowing, AuraBorder)
        )
    }

    Box(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clip(shape)
            .background(AuraSurfaceElevated.copy(alpha = 0.82f))
            .border(
                border = BorderStroke(1.dp, borderBrush),
                shape = shape
            )
            .padding(16.dp),
        content = content
    )
}
