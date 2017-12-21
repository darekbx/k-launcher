package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DOT_SIZE = 7F
        const val BOX_WIDTH = 120
        const val BOX_HEIGHT = 40
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
        canvas?.drawColor(Color.BLACK)
        if (mapSensor == null) return
        canvas?.let { canvas ->
            mapSensor?.let { mapSensor ->
                canvas.drawCircle(DOT_SIZE, DOT_SIZE, DOT_SIZE, paint)

                canvas.drawText("%.1fº".format(mapSensor.sensor?.currentMeasurements?.temperature), 30F, 18F, paint)
                //canvas.drawText("PM10: ${mapSensor.sensor?.currentMeasurements?.pm10}", 70F, 48F, paint)
                //canvas.drawText("PM2.5: ${mapSensor.sensor?.currentMeasurements?.pm25}", 70F, 72F, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(BOX_WIDTH, BOX_HEIGHT)
    }
}