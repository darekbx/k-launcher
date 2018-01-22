package com.klauncher.ui.main

import com.klauncher.airly.AirlyController
import com.klauncher.airly.AirlySensors
import com.klauncher.dotpad.DotsCount
import com.klauncher.extensions.threadToAndroid
import com.klauncher.model.rest.Sensor
import com.klauncher.model.rest.SensorError
import com.klauncher.zm.PollutionLevel
import io.reactivex.Observable

class MainPresenter(val view: MainContract.View): MainContract.Presenter {

    override fun loadSensors() {
        Observable
                .fromArray(AirlySensors.sensors)
                .flatMapIterable { mapSensors -> mapSensors }
                .doOnNext { it.sensor = fetchSensorData(it.airlyId) }
                .threadToAndroid()
                .subscribe(
                        { view.refreshSensor(it) },
                        { view.notifyError(it) })
    }

    fun fetchSensorData(sensorId: Int): Sensor? {
        try {
            val response = airlyController.airlyService.loadSensor(sensorId).execute()
            return when (response.isSuccessful) {
                true -> response.body()
                false -> SensorError("HTTP error code: {$response.code()}")
            }
        } catch (e: Exception) {
            return SensorError(e.message ?: "Unknown error")
        }
    }

    override fun loadPollution() {
        val pollutionLevel = PollutionLevel()
        pollutionLevel
                .loadPollution()
                .threadToAndroid()
                .subscribe(
                        { view.displayPollution(it) },
                        { view.notifyError(it) }
                )
    }

    override fun loadDotCount() {
        val count = dotsCount.countActiveDots()
        view?.let { view -> view.displayDotCount(count) }
    }

    private val airlyController by lazy {
        AirlyController().apply { setup() }
    }

    private val dotsCount by lazy {
        DotsCount()
    }
}