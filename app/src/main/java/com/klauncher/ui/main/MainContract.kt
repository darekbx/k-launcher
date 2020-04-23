package com.klauncher.ui.main

import android.content.Context
import android.graphics.Bitmap
import com.klauncher.api.imgw.WaterMarks
import com.klauncher.model.MapSensor

interface MainContract {

    interface View {

        fun getContext(): Context

        fun displaySensors(sensor: MapSensor)
        fun displayDotCount(count: Int)
        fun displayAntistorm(images: List<Bitmap>)
        fun displayWaterMarks(entries: List<WaterMarks.Entry>)

        fun notifyError(error: Throwable)
        fun cancelRefresh()
    }

    interface Presenter {

        fun loadSensors()
        fun loadDotCount()
        fun loadAntistorm()
        fun loadWaterMarks()
    }
}