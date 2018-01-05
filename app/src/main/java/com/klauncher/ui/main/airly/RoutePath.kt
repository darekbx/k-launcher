package com.klauncher.ui.main.airly

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.FloatProperty

class RoutePath: Drawable() {

    companion object {
        val points = arrayOf(
                252, 203, 253, 253, 254, 315, 360, 302, 361, 274, 407, 275, 405, 291, 432, 290,
                437, 258, 465, 254, 470, 283, 489, 281, 494, 290, 516, 286, 520, 328, 572, 318,
                590, 315, 604, 315, 623, 311, 730, 301, 750, 294, 769, 288, 820, 270, 844, 263,
                865, 313, 804, 337, 784, 342, 691, 389, 629, 423, 604, 436, 484, 564, 367, 525,
                365, 514, 282, 555, 265, 554, 257, 552, 249, 556, 230, 538, 223, 529, 224, 519,
                225, 511, 236, 490, 243, 470, 248, 445, 250, 427, 250, 403, 256, 382, 251, 381,
                217, 327, 209, 320, 137, 310, 167, 264, 189, 228, 206, 196, 251, 202
                )
    }

    override fun setAlpha(alpha: Int) { }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) { }

    val polygon = Polygon()

    var morphProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback.invalidateDrawable(this)
        }

    init {
        polygon.createPath()
    }

    val linePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.RED
    }

    val dotPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val pathLine = Path().apply {
        moveTo(0f, -2f)
        lineTo(40f, -2f)
        lineTo(40f, 2f)
        lineTo(0f, 2f)
        close()
    }

     override fun draw(canvas: Canvas) {

        //canvas.drawPath(polygon.path, linePaint)

        val phase = morphProgress * polygon.length * 2
        dotPaint.pathEffect = PathDashPathEffect(pathLine, polygon.length, phase, PathDashPathEffect.Style.MORPH)
        canvas.drawPath(polygon.path, dotPaint)
    }

    class Polygon() {

        private val pathMeasure = PathMeasure()
        val path = Path()

        val length by lazy(LazyThreadSafetyMode.NONE) {
            pathMeasure.setPath(path, false)
            pathMeasure.length
        }

        fun createPath() {
            with(path) {
                moveTo(points[0].toFloat(), points[1].toFloat() + 300)
                for (i in 2 until points.size step 2) {
                    lineTo(points[i].toFloat(), points[i + 1].toFloat() + 300)
                }
            }
        }
    }

    object MORPH_PROGRESS : FloatProperty<RoutePath>("morphProgress") {
        override fun setValue(drawable: RoutePath, morphProgress: Float) {
            drawable.morphProgress = morphProgress
        }

        override fun get(drawable: RoutePath) = drawable.morphProgress
    }
}