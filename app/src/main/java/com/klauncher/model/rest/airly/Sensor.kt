package com.klauncher.model.rest.airly

open class Sensor(
        val current: Measurement? = null,
        var rateLimitDay: Int = 0,
        var rateLimitMinute: Int = 0,
        var rateLimitRemainingDay: Int = 0,
        var rateLimitRemainingMinute: Int = 0)