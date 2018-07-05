package com.klauncher.ui.main

import com.klauncher.api.ifmeteo.IfWeather
import com.klauncher.api.airly.AirlyController
import com.klauncher.api.airly.AirlySensors
import com.klauncher.api.dotpad.DotsCount
import com.klauncher.extensions.threadToAndroid
import com.klauncher.model.rest.Sensor
import com.klauncher.model.rest.SensorError
import com.klauncher.api.zm.PollutionLevel
import com.klauncher.extensions.notNull
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MainPresenter(val view: MainContract.View): MainContract.Presenter {

    private var sensorsHandle: Disposable? = null

    override fun loadSensors() {
        sensorsHandle?.let {sensorsHandle ->
            if (!sensorsHandle?.isDisposed()) {
                sensorsHandle.dispose()
            }
        }
        sensorsHandle = Observable
                .fromArray(AirlySensors.sensors)
                .flatMapIterable { mapSensors -> mapSensors }
                .doOnNext { it.sensor = fetchSensorData(it.airlyId) }
                .threadToAndroid()
                .notNull(view)
                .subscribe(
                        { view.displaySensors(it) },
                        { view.notifyError(it) },
                        { sensorsHandle?.dispose() }
                )
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
                PollutionLevel()
                .loadPollution()
                .threadToAndroid()
                .notNull(view)
                .subscribe(
                        { view.displayPollution(it) },
                        { view.notifyError(it) }
                )
    }

    override fun loadDotCount() {
        val count = dotsCount.countActiveDots()
        view?.let { view -> view.displayDotCount(count) }
    }

    override fun loadIfWeather() {
        IfWeather()
                .currentConditions()
                .threadToAndroid()
                .notNull(view)
                .subscribe(
                        { view.displayIfWeather(it) },
                        { view.notifyError(it) }
                )
    }

    private val airlyController by lazy {
        AirlyController().apply { setup() }
    }

    private val dotsCount by lazy {
        DotsCount()
    }
}