package com.klauncher.model.rest.zm

data class ActualPollution(val pm10: Pollution,
                           val pm25: Pollution,
                           val time: String)