package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

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
                canvas.drawCircle(
                        7F,
                        7F,
                        14F,
                        paint)

                //canvas.drawText("TEMP: ${mapSensor.sensor?.currentMeasurements?.temperature}", 70F, 24F, paint)
                //canvas.drawText("PM10: ${mapSensor.sensor?.currentMeasurements?.pm10}", 70F, 48F, paint)
                //canvas.drawText("PM2.5: ${mapSensor.sensor?.currentMeasurements?.pm25}", 70F, 72F, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(240, 80)
    }
}