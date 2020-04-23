package com.klauncher.ui.main

import com.klauncher.api.airly.AirlyController
import com.klauncher.api.airly.AirlySensors
import com.klauncher.api.antistorm.AntiStorm
import com.klauncher.api.dotpad.DotsCount
import com.klauncher.api.imgw.WaterMarks
import com.klauncher.extensions.threadToAndroid
import com.klauncher.external.Preferences
import com.klauncher.model.rest.airly.Sensor
import com.klauncher.model.rest.SensorError
import com.klauncher.model.MapSensor
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainPresenter(val view: MainContract.View?): MainContract.Presenter {

    private var sensorsHandle: Disposable? = null
    private val averageMap = hashMapOf<Int, Double>()

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
                .doOnNext { obtainTrend(it) }
                .threadToAndroid()
                .subscribe(
                        { view?.displaySensors(it) },
                        { view?.notifyError(it) },
                        { sensorsHandle?.dispose() }
                )
    }

    fun obtainTrend(mapSensor: MapSensor) {
        if (averageMap.containsKey(mapSensor.airlyId)) {
            val previousValue = averageMap.getOrDefault(mapSensor.airlyId, 0.0)
            mapSensor.trend = mapSensor.calculatePMAverage().compareTo(previousValue)
        }
        averageMap.put(mapSensor.airlyId, mapSensor.calculatePMAverage())
    }

    fun fetchSensorData(sensorId: Int): Sensor? {
        try {
            val response = airlyController.airlyService.loadSensor(sensorId).execute()
            return when {
                response.isSuccessful -> {
                    response.body()?.apply {
                        applyRateLimits(response)
                    }
                }
                response.code() == 429 -> {
                    val sensor = Sensor()
                    sensor.applyRateLimits(response)
                    return sensor
                }
                else -> SensorError("HTTP error code: {$response.code()}")
            }
        } catch (e: Exception) {
            return SensorError(e.message ?: "Unknown error")
        }
    }

    private fun Sensor.applyRateLimits(response: Response<Sensor>) {
        val headers = response.headers()
        rateLimitDay = headers["X-RateLimit-Limit-day"]?.toIntOrNull() ?: 0
        rateLimitMinute = headers["X-RateLimit-Limit-minute"]?.toIntOrNull() ?: 0
        rateLimitRemainingDay = headers["X-RateLimit-Remaining-day"]?.toIntOrNull() ?: 0
        rateLimitRemainingMinute = headers["X-RateLimit-Remaining-minute"]?.toIntOrNull() ?: 0
    }

    override fun loadAntistorm() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AntiStorm()
                        .loadImages()
                        ?.run { view?.displayAntistorm(this) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun loadWaterMarks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                view?.getContext()?.let { context ->
                    val preferences = Preferences(context)
                    val waterMarks = WaterMarks(preferences)
                    waterMarks.loadReadings()?.let { entries ->
                        view?.displayWaterMarks(entries)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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