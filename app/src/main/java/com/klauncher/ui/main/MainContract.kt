package com.klauncher.ui.main

import android.content.Context
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.SensorError
import com.klauncher.model.rest.zm.ActualPollution

interface MainContract {

    interface View {

        fun getContext(): Context
        fun refreshSensor(sensor: MapSensor)
        fun displayPollution(actualPollution: ActualPollution)
        fun notifyError(error: Throwable)
    }

    interface Presenter {

        fun loadSensors()
        fun loadPollution()
    }
}