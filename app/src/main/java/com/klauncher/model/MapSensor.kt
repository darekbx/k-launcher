package com.klauncher.model

import com.klauncher.model.rest.airly.Sensor

data class MapSensor(
        val airlyId: Int,
        val name: String,
        val x: Int,
        val y: Int,
        var sensor: Sensor? = null)