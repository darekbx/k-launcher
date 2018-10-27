package com.klauncher.model

import android.text.TextUtils
import com.klauncher.model.rest.airly.Sensor

data class MapSensor(
        val airlyId: Int,
        val name: String,
        val x: Int,
        val y: Int,
        var sensor: Sensor? = null,
        var trend: Int = 0) {

    companion object {
        val PM_ARRAY = arrayOf("pm1", "pm10", "pm25")
    }

    fun getArrow() =
            when {
                trend > 0 -> "\u2191"
                trend < 0 -> "\u2193"
                else -> "-"
            }

    fun calculatePMAverage(): Double {
        val pmCount = PM_ARRAY.size.toDouble()
        val values = sensor?.current?.values
        val sum = values
                ?.filter { !TextUtils.isEmpty(it.name) }
                ?.filter { PM_ARRAY.contains(it.name.toLowerCase()) }
                ?.filter { it.value.toDoubleOrNull() != null }
                ?.sumByDouble { it.value.toDouble() } ?: pmCount
        return sum / pmCount
    }
}