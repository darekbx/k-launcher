package com.klauncher.ui.main

import com.klauncher.Utils
import com.klauncher.model.rest.airly.Value
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MainPresenterTest {

    @Test
    fun obtainTrend() {
        // Given
        val values1 = arrayListOf(
                Value("PM1", "13.5"),
                Value("PM25", "45.3"),
                Value("PM10", "24.9")
        )
        val values2 = arrayListOf(
                Value("PM1", "13.8"),
                Value("PM25", "45.9"),
                Value("PM10", "26.0")
        )
        val mapSensor1 = Utils().createMapSensor(values1)
        val mapSensor2 = Utils().createMapSensor(values2)
        val mainPresener = MainPresenter(mock { MainContract.View::class.java })

        // When
        assertEquals(0, mapSensor1.trend)
        mainPresener.obtainTrend(mapSensor1)
        mainPresener.obtainTrend(mapSensor2)

        // Then
        assertEquals(1, mapSensor2.trend)
        assertEquals("\u2191", mapSensor2.getArrow())
    }
}