package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.SensorError
import com.klauncher.model.rest.zm.Pollution

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DOT_SIZE = 22F
        const val BOX_WIDTH = 200
        const val BOX_HEIGHT = 98
    }

    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 22F
    }

    var mapSensor: MapSensor? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mapSensor == null) return
        canvas?.let { canvas ->
            mapSensor?.let { mapSensor ->
                when (mapSensor.sensor) {
                    is SensorError -> drawNoData(canvas)
                    else -> drawSensor(mapSensor, canvas)
                }
            }
        }
    }

    private fun drawSensor(mapSensor: MapSensor, canvas: Canvas) {
        drawPollutionLevel(mapSensor, canvas)

        mapSensor.sensor?.currentMeasurements?.pollutionLevel.let { pollutionLevel ->
            if (pollutionLevel != 0) {
                paint.color = Color.WHITE
                paint.shader = null

                paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas.drawText("%.1fº".format(mapSensor.sensor?.currentMeasurements?.temperature), 42F, 29F, paint)

                paint.typeface = Typeface.MONOSPACE

                paint.color = Pollution().translateToPollutionColor(mapSensor.sensor?.currentMeasurements?.pm10 ?: 0.0, 50.0)
                canvas.drawText("%.1fµg".format(mapSensor.sensor?.currentMeasurements?.pm10), 42F, 55F, paint)

                paint.color = Pollution().translateToPollutionColor(mapSensor.sensor?.currentMeasurements?.pm25 ?: 0.0, 25.0)
                canvas.drawText("%.1fµg".format(mapSensor.sensor?.currentMeasurements?.pm25), 42F, 82F, paint)
            }
        }
    }

    private fun drawPollutionLevel(mapSensor: MapSensor, canvas: Canvas) {
        val pollutionLevel = mapSensor.sensor?.currentMeasurements?.pollutionLevel ?: 0
        val colorHex = AirlyColors.from(pollutionLevel)

        with(paint) {
            color = Color.parseColor(colorHex)
            drawGradiontDot(canvas)
        }
    }

    private fun drawGradiontDot(canvas: Canvas) {
        //paint.shader = RadialGradient(DOT_SIZE, DOT_SIZE, 32F, paint.color, Color.TRANSPARENT, Shader.TileMode.MIRROR)
        with(paint) {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 3f
            canvas.drawCircle(DOT_SIZE, DOT_SIZE, 8F, this)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0f
        }
    }

    private fun drawNoData(canvas: Canvas) {
        paint.color = Color.parseColor(AirlyColors.NO_DATA.color)
        canvas.drawCircle(DOT_SIZE, DOT_SIZE, DOT_SIZE, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(BOX_WIDTH, BOX_HEIGHT)
    }
}