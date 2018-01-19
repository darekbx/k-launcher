package com.klauncher.ui.main.traffic

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.FloatProperty

class TrafficDrawable : Drawable() {

    companion object {
        val DRAW_DEBUG_LINE = false
    }

    override fun setAlpha(alpha: Int) {}
    override fun getOpacity() = PixelFormat.TRANSLUCENT
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    val routes = ArrayList<TrafficRoute>()

    init {
        initializeRoutes()
    }

    var morphProgress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback.invalidateDrawable(this)
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

    private fun initializeRoutes() {
        for (routePoints in Routes.list) {
            routes.add(TrafficRoute().apply { initializePath(routePoints) })
        }
    }

    override fun draw(canvas: Canvas) {
        for (route in routes) {
            if (DRAW_DEBUG_LINE) canvas.drawPath(route.path, linePaint)
            dotPaint.pathEffect = PathDashPathEffect(pathLine, route.length, route.phase(morphProgress), PathDashPathEffect.Style.MORPH)
            canvas.drawPath(route.path, dotPaint)
        }
    }


    object MORPH_PROGRESS : FloatProperty<TrafficDrawable>("morphProgress") {
        override fun setValue(drawable: TrafficDrawable, morphProgress: Float) {
            drawable.morphProgress = morphProgress
        }

        override fun get(drawable: TrafficDrawable) = drawable.morphProgress
    }
}