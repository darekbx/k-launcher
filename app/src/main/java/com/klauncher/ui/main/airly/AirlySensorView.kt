package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.SensorError

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DOT_SIZE = 20F
        const val BOX_WIDTH = 160
        const val BOX_HEIGHT = 80
    }

    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 26F
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

        paint.color = Color.WHITE
        paint.shader = null
        canvas.drawText("%.1fº".format(mapSensor.sensor?.currentMeasurements?.temperature), 42F, 29F, paint)


        //canvas.drawText("PM10: ${mapSensor.sensor?.currentMeasurements?.pm10}", 30F, 44F, paint)
        //canvas.drawText("PM2.5: ${mapSensor.sensor?.currentMeasurements?.pm25}", 30F, 72F, paint)
    }

    private fun drawPollutionLevel(mapSensor: MapSensor, canvas: Canvas) {
        val pollutionLevel = mapSensor.sensor?.currentMeasurements?.pollutionLevel ?: 0
        val colorHex = AirlyColors.from(pollutionLevel)

        with (paint) {
            color = Color.parseColor(colorHex)
            drawGradiontDot(canvas)
        }
    }

    private fun Paint.drawGradiontDot(canvas: Canvas) {
        shader = RadialGradient(DOT_SIZE, DOT_SIZE, 30F, color, Color.TRANSPARENT, Shader.TileMode.MIRROR)
        canvas.drawCircle(DOT_SIZE, DOT_SIZE, 30F, this)
    }

    private fun drawNoData(canvas: Canvas) {
        paint.color = Color.parseColor(AirlyColors.NO_DATA.color)
        canvas.drawCircle(DOT_SIZE, DOT_SIZE, DOT_SIZE, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(BOX_WIDTH, BOX_HEIGHT)
    }
}