package com.klauncher.model.rest

data class Measurements(
        val airQualityIndex: Double,
        val humidity: Double,
        val measurementTime: String,
        val pm1: Double,
        val pm10: Double,
        val pm25: Double,
        val pollutionLevel: Int,
        val pressure: Double,
        val temperature: Double)