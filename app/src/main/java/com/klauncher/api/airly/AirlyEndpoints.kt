package com.klauncher.api.airly

import com.klauncher.model.rest.airly.Sensor
import com.klauncher.model.rest.SimpleSensor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AirlyEndpoints {

    @Headers(
        "Accept: application/json",
        "apikey: ${AirlyController.AUTHENTICATION_KEY}"
    )
    @GET("/v2/installations")
    fun loadSensorsByArea(
            @Query("southwestLat") southwestLat: String,
            @Query("southwestLong") southwestLong: String,
            @Query("northeastLat") northeastLat: String,
            @Query("northeastLong") northeastLong: String
            ): Call<List<SimpleSensor>>

    @Headers(
            "Accept: application/json",
            "apikey: ${AirlyController.AUTHENTICATION_KEY}"
    )
    @GET("/v2/measurements/installation")
    fun loadSensor(
            @Query("installationId") installationId: Int
    ): Call<Sensor>
}