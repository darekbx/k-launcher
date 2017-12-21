package com.klauncher.ui.main

import com.klauncher.airly.AirlyController
import com.klauncher.airly.AirlySensors
import com.klauncher.extensions.threadToAndroid
import com.klauncher.model.rest.Sensor
import com.klauncher.model.rest.SensorError
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

    private val airlyController by lazy {
        AirlyController().apply { setup() }
    }
}