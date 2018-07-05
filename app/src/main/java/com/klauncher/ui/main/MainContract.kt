package com.klauncher.ui.main

import android.content.Context
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.ifmeteo.WeatherConditions
import com.klauncher.model.rest.zm.ActualPollution

interface MainContract {

    interface View {

        fun getContext(): Context

        fun displaySensors(sensor: MapSensor)
        fun displayPollution(actualPollution: ActualPollution)
        fun displayDotCount(count: Int)
        fun displayIfWeather(weatherConditions: WeatherConditions)

        fun notifyError(error: Throwable)
        fun cancelRefresh()
    }

    interface Presenter {

        fun loadSensors()
        fun loadPollution()
        fun loadDotCount()
        fun loadIfWeather()
    }
}