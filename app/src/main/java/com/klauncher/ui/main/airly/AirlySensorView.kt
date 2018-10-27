package com.klauncher.ui.main.airly

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.airly.Measurement
import com.klauncher.model.rest.SensorError
import com.klauncher.model.rest.airly.Index
import com.klauncher.model.rest.zm.Pollution

class AirlySensorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DOT_SIZE = 22F
        const val BOX_WIDTH = 600
        const val BOX_HEIGHT = 38
        const val OFFSET = 110f
    }

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.MONOSPACE
        textSize = 22F
    }

    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
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
                    translate(30f, 20f)

                    drawTemperature(currentMeasurements, this)
                    drawMeasurement(currentMeasurements, "PM1", this)
                    drawMeasurement(currentMeasurements, "PM10", this)
                    drawMeasurement(currentMeasurements, "PM25", this)

                    canvas.translate(OFFSET, 0F)
                    canvas.drawText(mapSensor.getArrow(), 0F, 9F, textPaint)

                    restore()
                }
            } else {
                drawDot(canvas, false)
            }
        }
    }

    private fun drawTemperature(currentMeasurement: Measurement, canvas: Canvas) {
        canvas.translate(OFFSET, 0F)
        currentMeasurement.values
                .firstOrNull { it.name == "TEMPERATURE" }
                ?.let { temp ->
                    textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    textPaint.color = Color.WHITE
                    canvas.drawText("%.1fº ".format(temp.value.toDouble()), 0F, 9F, textPaint)
                }
    }

    private fun drawMeasurement(currentMeasurement: Measurement, key: String, canvas: Canvas) {
        canvas.translate(OFFSET, 0F)
        currentMeasurement.values
                .firstOrNull { it.name == key }
                ?.value?.toDouble()
                ?.let { value ->
                    val colorString = currentMeasurement.indexes.firstOrNull()
                    textPaint.typeface = Typeface.MONOSPACE
                    textPaint.color = extractColor(colorString, value)
                    canvas.drawText("%.1fµg".format(value), 0F, 9F, textPaint)
                }
    }

    private fun extractColor(colorString: Index?, value: Double) = when (colorString) {
        null -> pollution.translateToPollutionColor(value, 50.0)
        else -> Color.parseColor(colorString.color)
    }

    private fun drawDot(canvas: Canvas, hasData: Boolean = true) {
        with(paint) {
            color = if (hasData) Color.WHITE else Color.GRAY
            canvas.drawCircle(DOT_SIZE, DOT_SIZE, 8F, this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(BOX_WIDTH, BOX_HEIGHT)
    }

    private val pollution by lazy {
        Pollution()
    }
}