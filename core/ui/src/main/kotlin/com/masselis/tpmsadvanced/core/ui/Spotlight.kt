package com.masselis.tpmsadvanced.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt


@Suppress("LongMethod")
@OptIn(ExperimentalTextApi::class)
@Composable
public fun Spotlight(
    center: Offset,
    radius: Float,
    text: AnnotatedString,
    onSpotlight: () -> Unit,
    modifier: Modifier = Modifier,
    onClickOutside: () -> Unit = {},
    outerColor: Color = Color.Black.copy(alpha = 0.7f),
    textPadding: Float = 0f,
    textStyle: TextStyle = TextStyle.Default,
    textWidth: Int = radius.times(3).roundToInt(),
    animationSpec: AnimationSpec<Float> = tween(3_000),
) {
    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val (width, height) = remember { with(density) { maxWidth.toPx() to maxHeight.toPx() } }
        val currentSize = remember {
            val farthestXBorder = (width - center.x).takeIf { it > width / 2 } ?: center.x
            val farthestYBorder = (height - center.y).takeIf { it > height / 2 } ?: center.y
            val hypotenuse = (farthestXBorder.pow(2) + farthestYBorder.pow(2)).pow(0.5f)
            Animatable(hypotenuse)
        }
        val textMeasurer = rememberTextMeasurer()
        val textTopLeft = remember {
            val result = textMeasurer.measure(
                text = text,
                style = textStyle,
                constraints = Constraints(maxWidth = textWidth)
            )
            Offset(
                (center.x - (result.size.width / 2f)).coerceIn(
                    0f + textPadding,
                    width - result.size.width - textPadding
                ),
                center.y + radius + textPadding
            )
        }
        LaunchedEffect("ANIM") {
            currentSize.animateTo(radius, animationSpec)
        }
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    val rect = RoundRect(
                        Rect(center, radius),
                        CornerRadius(radius)
                    )
                    detectTapGestures {
                        if (rect.contains(it))
                            onSpotlight()
                        else
                            onClickOutside()
                    }
                }
        ) {
            clipPath(
                path = Path().apply {
                    addOval(
                        Rect(
                            center = center,
                            radius = currentSize.value,
                        )
                    )
                },
                clipOp = ClipOp.Difference
            ) {
                drawRect(SolidColor(outerColor))
                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = textTopLeft,
                    style = textStyle,
                    size = Size(
                        width = textWidth.toFloat(),
                        height = ceil(this.size.height - textTopLeft.y)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSpotlight() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    ) {
        with(LocalDensity.current) {
            val string =
                remember { AnnotatedString("Check this out") }
            Spotlight(
                center = Offset(300.dp.toPx(), 100.dp.toPx()),
                radius = 50.dp.toPx(),
                text = string,
                onSpotlight = {},
                textPadding = 8.dp.toPx(),
                textStyle = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSpotlightLongText() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    ) {
        with(LocalDensity.current) {
            val string = remember {
                @Suppress("MaxLineLength")
                AnnotatedString("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            }
            Spotlight(
                center = Offset(50.dp.toPx(), 100.dp.toPx()),
                radius = 50.dp.toPx(),
                text = string,
                onSpotlight = {},
                textPadding = 8.dp.toPx(),
                textStyle = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
