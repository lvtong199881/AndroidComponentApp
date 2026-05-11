package com.mohanlv.common

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt

/**
 * Shape DSL 构建器
 *
 * 使用示例：
 * ```
 * shape {
 *     solidColorInt = Color.RED
 *     cornerRadius = 16f
 *     gradient {
 *         gradientColorsInt = listOf(Color.WHITE, Color.YELLOW, Color.BLACK)
 *         gradientOffsets = listOf(0f, 0.1f, 1f)
 *         gradientColorsDefault = listOf(Color.WHITE, Color.BLACK)
 *     }
 *     stroke {
 *         strokeColorInt = Color.BLACK
 *         strokeWidth = 1f
 *     }
 * }
 * ```
 */
class ShapeBuilder {
    var cornerRadius: Float = 0f

    var cornerTopLeft: Float? = null
    var cornerTopRight: Float? = null
    var cornerBottomLeft: Float? = null
    var cornerBottomRight: Float? = null

    var solidColorInt: Int? = null
    var solidColorString: String? = null
    var solidColorDefault: Int = Color.TRANSPARENT

    val gradient = GradientConfig()
    val stroke = StrokeConfig()

    class GradientConfig {
        var orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT

        var gradientColorsInt: List<Int>? = null
        var gradientColorsString: List<String>? = null
        var gradientOffsets: List<Float>? = null
        var gradientColorsDefault: List<Int>? = null

        operator fun invoke(block: GradientConfig.() -> Unit) {
            block()
        }

        fun toColors(): IntArray? {
            val colors = gradientColorsInt ?: gradientColorsString?.mapNotNull { safeParseColor(it) }
            return colors?.toIntArray() ?: gradientColorsDefault?.toIntArray()
        }
    }

    class StrokeConfig {
        var strokeColorInt: Int? = null
        var strokeColorString: String? = null
        var strokeWidth: Int = 0
        var strokeColorDefault: Int = Color.TRANSPARENT

        operator fun invoke(block: StrokeConfig.() -> Unit) {
            block()
        }
    }
}

private fun safeParseColor(colorString: String): Int? {
    return try {
        colorString.toColorInt()
    } catch (e: Exception) {
        null
    }
}

/**
 * shape DSL 函数
 */
fun shape(block: ShapeBuilder.() -> Unit): GradientDrawable {
    val builder = ShapeBuilder().apply(block)

    // gradient
    val gradientColors = builder.gradient.toColors()
    val drawable = GradientDrawable()
    drawable.orientation = builder.gradient.orientation
    if (gradientColors != null) {
        // 优先使用渐变色
        val offsets = builder.gradient.gradientOffsets?.toFloatArray()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.setColors(gradientColors, offsets)
        } else {
            drawable.colors = gradientColors
        }
    } else {
        // 使用纯色
        val solidColor = builder.solidColorInt ?: builder.solidColorString?.let { safeParseColor(it) } ?: builder.solidColorDefault
        drawable.setColor(solidColor)
    }

    // corner
    val hasCustomCorners = builder.cornerTopLeft != null || builder.cornerTopRight != null ||
            builder.cornerBottomLeft != null || builder.cornerBottomRight != null
   if (hasCustomCorners) {
            val tl = builder.cornerTopLeft ?: 0f
            val tr = builder.cornerTopRight ?: 0f
            val bl = builder.cornerBottomLeft ?: 0f
            val br = builder.cornerBottomRight ?: 0f
            drawable.cornerRadii = floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
   } else {
       drawable.cornerRadius = builder.cornerRadius
    }

    // stroke
    val strokeColor = builder.stroke.strokeColorInt ?: builder.stroke.strokeColorString?.let { safeParseColor(it) } ?: builder.stroke.strokeColorDefault
    if (builder.stroke.strokeWidth > 0) {
        drawable.setStroke(builder.stroke.strokeWidth, strokeColor)
    }

    return drawable
}
