package com.klauncher

import com.klauncher.model.MapSensor
import com.klauncher.model.rest.airly.Measurement
import com.klauncher.model.rest.airly.Sensor
import com.klauncher.model.rest.airly.Value

class Utils {

    fun createMapSensor(values: List<Value>): MapSensor {
        val measurement = Measurement(values, arrayListOf(), "")
        val sensor = Sensor(current = measurement)
        val mapSensor = MapSensor(1, "", 0, 0, sensor)
        return mapSensor
    }
}