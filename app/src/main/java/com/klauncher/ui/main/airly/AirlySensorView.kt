package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.airly.Measurement
import com.klauncher.model.rest.SensorError
import com.klauncher.model.rest.zm.Pollution

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DOT_SIZE = 22F
        const val BOX_WIDTH = 600
        const val BOX_HEIGHT = 38
        const val OFFSET = 110f
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
        mapSensor.sensor?.current?.let { currentMeasurements ->
            if (currentMeasurements.values.isNotEmpty()) {
                drawDot(canvas)

                with(canvas) {
                    save()
                    translate(130f, 20f)

                    drawTemperature(currentMeasurements, this)
                    translate(OFFSET, 0F)
                    drawMeasurement(currentMeasurements, "PM1", this)
                    translate(OFFSET, 0F)
                    drawMeasurement(currentMeasurements, "PM10", this)
                    translate(OFFSET, 0F)
                    drawMeasurement(currentMeasurements, "PM25", this)

                    restore()
                }
            } else {
                drawDot(canvas, false);
            }
        }
    }

    private fun drawTemperature(currentMeasurement: Measurement, canvas: Canvas) {
        val temp = currentMeasurement.values.first { it.name == "TEMPERATURE" }
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.color = when (temp.value.toDouble()) {
            0.0 -> Color.GRAY
            else -> Color.WHITE
        }
        canvas.drawText("%.1fº ".format(temp.value.toDouble()), 0F, 9F, paint)
    }

    private fun drawMeasurement(currentMeasurement: Measurement, key: String, canvas: Canvas) {
        val value = currentMeasurement.values.first { it.name == key }.value.toDouble()
        val colorString = currentMeasurement.indexes.firstOrNull()
        paint.typeface = Typeface.MONOSPACE
        if (colorString != null) {
            paint.color = Color.parseColor(colorString.color)
        } else {
            paint.color = pollution.translateToPollutionColor(value, 50.0)
        }
        canvas.drawText("%.1fµg".format(value), 0F, 9F, paint)
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