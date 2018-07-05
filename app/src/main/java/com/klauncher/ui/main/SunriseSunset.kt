package com.klauncher.ui.main

import com.klauncher.extensions.format
import net.time4j.PlainDate
import net.time4j.calendar.astro.SolarTime

class SunriseSunset {

    companion object {
        val WARSAW_LATITUDE = 52.13
        val WARSAW_LONGITUDE = 21.00
    }

    fun load(): Pair<String, String> {
        with (SolarTime.ofLocation(WARSAW_LATITUDE, WARSAW_LONGITUDE)) {
            val sunrise = PlainDate.nowInSystemTime().get(sunrise()).toLocalTimestamp()
            val sunset = PlainDate.nowInSystemTime().get(sunset()).toLocalTimestamp()
            return Pair(
                    "${sunrise.hour}:${sunrise.minute.format(2)}",
                    " ${sunset.hour}:${sunset.minute.format(2)}"
            )
        }
    }
}