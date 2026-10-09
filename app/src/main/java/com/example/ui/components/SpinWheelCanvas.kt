package com.example.ui.components

import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.SpinWheel
import com.example.ui.theme.HaynerCyan
import com.example.ui.theme.HaynerGold
import com.example.ui.theme.HaynerPurple
import com.example.ui.theme.LocalHaynerColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpinWheelCanvas(
    wheel: SpinWheel,
    rotationDegrees: Float,
    needleDeflectionDegrees: Float = 0f,
    modifier: Modifier = Modifier,
    onWheelTap: (() -> Unit)? = null
) {
    val themeColors = LocalHaynerColors.current
    val segments = wheel.segments
    val segmentCount = segments.size.coerceAtLeast(1)
    val sweepAngle = 360f / segmentCount

    // Create and remember Android Paints for performance
    val textPaint = remember(segmentCount) {
        AndroidPaint().apply {
            color = AndroidColor.WHITE
            textSize = when {
                segmentCount > 10 -> 28f
                segmentCount > 6 -> 34f
                else -> 40f
            }
            isAntiAlias = true
            textAlign = AndroidPaint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(8f, 0f, 2f, AndroidColor.parseColor("#80000000"))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .testTag("spin_wheel_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onWheelTap?.invoke() }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = (size.width / 2f) - 14.dp.toPx()
            val innerRadius = outerRadius * 0.26f

            // 1. Draw glowing outer rim shadow / aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        themeColors.primary.copy(alpha = 0.45f),
                        themeColors.secondary.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius + 14.dp.toPx()
                ),
                radius = outerRadius + 12.dp.toPx(),
                center = center
            )

            // 2. Draw metallic outer wheel border
            drawCircle(
                color = Color(0xFF1E163B),
                radius = outerRadius + 6.dp.toPx(),
                center = center
            )
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        HaynerPurple,
                        HaynerCyan,
                        HaynerGold,
                        HaynerPurple
                    ),
                    center = center
                ),
                radius = outerRadius + 6.dp.toPx(),
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // 3. Draw segments with rotation
            val wheelRect = Size(outerRadius * 2, outerRadius * 2)
            val wheelTopLeft = Offset(center.x - outerRadius, center.y - outerRadius)

            for (i in 0 until segmentCount) {
                val segment = segments[i]
                val startAngle = (i * sweepAngle) + rotationDegrees
                val segmentColor = try {
                    Color(android.graphics.Color.parseColor(segment.colorHex))
                } catch (_: Exception) {
                    if (i % 2 == 0) HaynerPurple else HaynerCyan
                }

                // Sector Arc
                drawArc(
                    color = segmentColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = wheelTopLeft,
                    size = wheelRect
                )

                // Shading overlay to give 3D depth to segments
                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f)
                        ),
                        center = center,
                        radius = outerRadius
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = wheelTopLeft,
                    size = wheelRect
                )

                // Divider spoke line
                val spokeRad = Math.toRadians(startAngle.toDouble())
                val spokeEnd = Offset(
                    (center.x + outerRadius * cos(spokeRad)).toFloat(),
                    (center.y + outerRadius * sin(spokeRad)).toFloat()
                )
                drawLine(
                    color = Color(0x66FFFFFF),
                    start = center,
                    end = spokeEnd,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 4. Draw segment labels inside sectors
            drawIntoCanvas { canvas ->
                val native = canvas.nativeCanvas
                for (i in 0 until segmentCount) {
                    val segment = segments[i]
                    val midAngle = (i * sweepAngle) + (sweepAngle / 2f) + rotationDegrees

                    native.save()
                    native.rotate(midAngle, center.x, center.y)

                    // Text position: near the outer rim pointing inward
                    val textX = center.x + outerRadius - 16.dp.toPx()
                    val textY = center.y + (textPaint.textSize * 0.35f)

                    // Truncate label if too long for slice
                    val label = if (segment.label.length > 12) {
                        segment.label.take(10) + "…"
                    } else segment.label

                    native.drawText(label, textX, textY, textPaint)
                    native.restore()
                }
            }

            // 5. Draw decorative studs / light bulbs along outer edge
            val studCount = segmentCount * 2
            val studAngle = 360f / studCount
            for (s in 0 until studCount) {
                val rad = Math.toRadians((s * studAngle + rotationDegrees).toDouble())
                val studPos = Offset(
                    (center.x + (outerRadius + 3.dp.toPx()) * cos(rad)).toFloat(),
                    (center.y + (outerRadius + 3.dp.toPx()) * sin(rad)).toFloat()
                )
                val studColor = if (s % 2 == 0) HaynerGold else Color.White
                drawCircle(
                    color = studColor,
                    radius = 2.5.dp.toPx(),
                    center = studPos
                )
            }

            // 6. Center Hub with "H" logo
            drawCircle(
                color = Color(0xFF0F0B1E),
                radius = innerRadius,
                center = center
            )
            drawCircle(
                brush = Brush.linearGradient(
                    listOf(HaynerCyan, HaynerPurple, HaynerGold)
                ),
                radius = innerRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            // Inner glow core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HaynerCyan.copy(alpha = 0.5f),
                        HaynerPurple.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = innerRadius
                ),
                radius = innerRadius * 0.85f,
                center = center
            )

            // Letter 'H' in the center hub
            drawIntoCanvas { canvas ->
                val hubPaint = AndroidPaint().apply {
                    color = AndroidColor.WHITE
                    textSize = innerRadius * 0.95f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = AndroidPaint.Align.CENTER
                    isAntiAlias = true
                    setShadowLayer(10f, 0f, 0f, AndroidColor.parseColor("#00E5FF"))
                }
                canvas.nativeCanvas.drawText("H", center.x, center.y + (hubPaint.textSize * 0.35f), hubPaint)
            }

            // 7. Top Fixed Indicator Needle (pointing down at 270 degrees / 12 o'clock)
            drawPointerNeedle(
                center = center,
                wheelRadius = outerRadius,
                deflectionDegrees = needleDeflectionDegrees
            )
        }
    }
}

/**
 * Draws the high-contrast golden pointer needle with spring deflection at the top of the wheel.
 */
private fun DrawScope.drawPointerNeedle(
    center: Offset,
    wheelRadius: Float,
    deflectionDegrees: Float
) {
    val needleBaseY = center.y - wheelRadius - 10.dp.toPx()
    val needleTipY = center.y - wheelRadius + 16.dp.toPx()
    val needleCenterX = center.x

    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        native.save()
        // Rotate around base of pointer for realistic mechanical flap/tick
        native.rotate(deflectionDegrees, needleCenterX, needleBaseY)

        val path = Path().apply {
            moveTo(needleCenterX, needleTipY) // Pointing down into segment
            lineTo(needleCenterX - 14.dp.toPx(), needleBaseY - 6.dp.toPx())
            lineTo(needleCenterX + 14.dp.toPx(), needleBaseY - 6.dp.toPx())
            close()
        }

        // Pointer shadow
        drawPath(
            path = path,
            color = Color.Black.copy(alpha = 0.4f)
        )

        // Golden body
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(HaynerGold, Color(0xFFF59E0B), Color(0xFFB45309))
            )
        )

        // Pointer border
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.9f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Pivot cap
        drawCircle(
            color = Color(0xFF1E1B4B),
            radius = 6.dp.toPx(),
            center = Offset(needleCenterX, needleBaseY - 6.dp.toPx())
        )
        drawCircle(
            color = HaynerGold,
            radius = 3.dp.toPx(),
            center = Offset(needleCenterX, needleBaseY - 6.dp.toPx())
        )

        native.restore()
    }
}
