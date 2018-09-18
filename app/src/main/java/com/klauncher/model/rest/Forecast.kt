package com.klauncher.model.rest

import com.klauncher.model.rest.airly.Measurement

data class Forecast(
        val fromDateTime: String,
        val tillDateTime: String,
        val measurement: Measurement)
