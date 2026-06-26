package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashView(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "splash_fade_in"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 0.9f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000) // 2-second handler before navigating
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate900, Slate800)
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Elegant branding logo element
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(listOf(CosmicBlue, CosmicIndigo)),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Slate100,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MCNSMM",
                style = MaterialTheme.typography.displayMedium,
                color = Slate100,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_app_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "All-in-One Digital & Web Development",
                style = MaterialTheme.typography.titleMedium,
                color = CosmicEmerald,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.testTag("splash_agency_tag")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Premium SMM Panel Solution",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate400,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Simple modifier extension for scaling easily
private fun Modifier.scale(scale: Float): Modifier = graphicsLayer(scaleX = scale, scaleY = scale)
