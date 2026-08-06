package me.magnum.melonds.ui.romlist.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun isEnhancedRom(romName: String, fileName: String): Boolean {
    val identity = "$romName $fileName".uppercase()
    return "MARIO 64 DS" in identity &&
        "USA" !in identity &&
        "JAP" !in identity &&
        "JPN" !in identity &&
        "KOR" !in identity
}

@Composable
fun Modifier.enhancedBorder(enabled: Boolean): Modifier {
    if (!enabled) return this
    val transition = rememberInfiniteTransition(label = "enhanced_border")
    val alpha by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "enhanced_border_alpha",
    )
    return border(BorderStroke(4.dp, Color(0xFFFFC107).copy(alpha = alpha)))
}

@Composable
fun EnhancedLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .background(Color(0xFFFFC107))
            .semantics { contentDescription = "Enhanced" }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Enhanced",
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
        )
    }
}
