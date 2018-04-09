package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.Measurements
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
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.MONOSPACE
        textSize = 22F
    }

    var mapSensor: MapSensor? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mapSensor == null) return
        canvas?.let { canvas ->
            mapSensor?.let { mapSensor ->
                when (mapSensor.sensor) {
                    is SensorError -> drawDot(canvas, false)
                    else -> drawSensor(mapSensor, canvas)
                }
            }
        }
    }

    private fun drawSensor(mapSensor: MapSensor, canvas: Canvas) {

        mapSensor.sensor?.currentMeasurements?.let { currentMeasurements ->
            if (currentMeasurements.pollutionLevel != 0) {
                drawDot(canvas)

                with(canvas) {
                    save()
                    translate(130f, 20f)

                    drawTemperature(currentMeasurements, this)
                    drawPM10Measurement(currentMeasurements, this)
                    drawPM25Measurement(currentMeasurements, this)

                    restore()
                }
            }else {
                drawDot(canvas, false);
            }
        }
    }

    private fun drawTemperature(currentMeasurements: Measurements, canvas: Canvas) {
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.color = when (currentMeasurements.temperature) {
            0.0 -> Color.GRAY
            else -> Color.WHITE
        }
        canvas.drawText("%.1fº ".format(currentMeasurements.temperature), 0F, 9F, paint)
    }

    private fun drawPM10Measurement(currentMeasurements: Measurements, canvas: Canvas) {
        paint.typeface = Typeface.MONOSPACE
        paint.color = pollution.translateToPollutionColor(currentMeasurements.pm10, 50.0)
        canvas.drawText("%.1fµg".format(currentMeasurements.pm10), 0F, 35F, paint)
    }


    private fun drawPM25Measurement(currentMeasurements: Measurements, canvas: Canvas) {
        paint.typeface = Typeface.MONOSPACE
        paint.color = pollution.translateToPollutionColor(currentMeasurements.pm25, 25.0)
        canvas.drawText("%.1fµg".format(currentMeasurements.pm25), 0F, 62F, paint)
    }

    private fun drawDot(canvas: Canvas, hasData: Boolean = true) {
        with(paint) {
            color = if (hasData) Color.WHITE else Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 3f
            canvas.drawCircle(DOT_SIZE, DOT_SIZE, 8F, this)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(BOX_WIDTH, BOX_HEIGHT)
    }

    private val pollution by lazy {
        Pollution()
    }
}