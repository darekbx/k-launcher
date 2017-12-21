package com.klauncher.model.rest

data class Forecast(
        val fromDateTime: String,
        val tillDateTime: String,
        val measurements: Measurements)
