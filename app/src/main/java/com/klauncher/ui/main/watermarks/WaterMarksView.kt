package com.klauncher.ui.main.watermarks

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.api.imgw.WaterMarks
import java.lang.Double.max

class WaterMarksView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        private val RED = Color.parseColor("#f44336")
        private val GREEN = Color.parseColor("#4caf50")
    }
    private val chartPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        alpha = 150
        strokeWidth = 1F
    }

    private val whitePaint = Paint().apply {
        isAntiAlias = true
        color = Color.argb(100, 255, 255, 255)
        strokeWidth = 1F
    }

    var entries: List<WaterMarks.Entry>? = null
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        entries?.let { entries ->
            canvas?.let { canvas ->
                val maxValue = entries
                        .map { max(it.currentLevel.toDouble(), it.previousLevel.toDouble()) }
                        .max() ?: 1.0

                val h = height - 6
                val xRatio = width / (entries.size - 1) - 1
                val yRatio = h / maxValue

                var first = Point(0, h - (entries.first().currentLevel * yRatio).toInt())
                var x = 0F

                drawLines(yRatio, canvas)

                entries.drop(1).forEach { entry ->
                    val yValue = h - (entry.currentLevel * yRatio.toFloat())

                    chartPaint.color = when {
                        entry.currentLevel > entry.previousLevel -> GREEN
                        entry.currentLevel < entry.previousLevel-> RED
                        else-> Color.WHITE
                    }

                    when (entry.name.toLowerCase() == "wars") {
                        true -> {
                            chartPaint.alpha = 250
                            chartPaint.strokeWidth = 1.5f
                        }
                        else -> {
                            chartPaint.alpha = 180
                            chartPaint.strokeWidth = 1f
                        }
                    }

                    canvas.drawLine(
                            first.x.toFloat(), first.y.toFloat(),
                            x, yValue,
                            chartPaint)
                    first.x = x.toInt()
                    first.y = yValue.toInt()
                    x += xRatio
                }
            }
        }
    }

    private fun drawLines(yRatio: Double, canvas: Canvas) {
        val h = height - 6
        listOf(0 + 5, 100, 200, 300, 400, 500 - 5).forEach {
            val y = h - it * yRatio.toFloat()
            val scale = when (it) {
                5 -> 0
                495 -> 500
                else -> it
            }
            canvas.drawLine(0F, y, width.toFloat() - 50, y, whitePaint)
            canvas.drawText("${Math.ceil(scale / 100.0).toInt()}m", width.toFloat() - 45, y + 4, whitePaint)
        }
    }
}