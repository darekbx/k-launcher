package com.klauncher.api.airly

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AirlyController {

    companion object {
        const val AUTHENTICATION_KEY = "HHGiCIvm1883USplSv3VFLRhsdDwSSvt"//"5a7d1c45f1f74cdea453cf9d0794b622"
        const val END_POINT = "https://airapi.airly.eu"
    }

    lateinit var airlyService: AirlyEndpoints
        private set

    constructor() {
        setup()
    }

    fun setup() {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(END_POINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        airlyService = retrofit.create(AirlyEndpoints::class.java)
    }
}