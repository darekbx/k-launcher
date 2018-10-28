package com.klauncher.model

import com.klauncher.Utils
import com.klauncher.model.rest.airly.Value
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MapSensorTest {

    @Test
    fun getArrow() {
    }

    @Test
    fun calculatePMAverage() {
        // Given
        val values = arrayListOf(
                Value("PM1", "13.5"),
                Value("PM25", "45.3"),
                Value("PM10", "24.9")
        )
        val mapSensor = Utils().createMapSensor(values)

        // When / Then
        assertEquals(27.89, mapSensor.calculatePMAverage(), 0.01)
    }
}