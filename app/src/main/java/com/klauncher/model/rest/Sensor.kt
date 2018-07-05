package com.klauncher.model.rest

open class Sensor(
        val currentMeasurements: Measurements? = null,
        val forecast: List<Forecast>? = null)