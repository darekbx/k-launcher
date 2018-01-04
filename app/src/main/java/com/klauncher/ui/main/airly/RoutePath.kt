package com.klauncher.ui.main.airly

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.FloatProperty

class RoutePath: Drawable() {

    companion object {
        val points = listOf(
                Point(244, 500),
                Point(248, 614),
                Point(355, 599),
                Point(364, 660),
                Point(364, 660),
                Point(364, 660),
                Point(361, 684),
                Point(489, 631),
                Point(568, 613),
                Point(723, 593),
                Point(840, 593),
                Point(858, 612),
                Point(948, 582)
        )
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    val polygon = Polygon()

    var dotProgress = 0f
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

        val phase = polygon.initialPhase + dotProgress * polygon.length * 2
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

        val initialPhase by lazy(LazyThreadSafetyMode.NONE) {
            0F
        }

        fun createPath() {
            with(path) {
                kotlin.with(points[0]) {
                    moveTo(x.toFloat(), y.toFloat())
                }
                for (i in 1 until points.size) {
                    kotlin.with(points[i]) {
                        lineTo(x.toFloat(), y.toFloat())
                    }
                }
            }
        }
    }

    object DOT_PROGRESS : FloatProperty<RoutePath>("dotProgress") {
        override fun setValue(drawable: RoutePath, dotProgress: Float) {
            drawable.dotProgress = dotProgress
        }

        override fun get(drawable: RoutePath) = drawable.dotProgress
    }
}