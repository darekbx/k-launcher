package com.klauncher.ui.main.screenon

import com.klauncher.external.Preferences
import java.util.*

open class ScreenTime(val preferences: Preferences) {

    var activeTime = 0L

    fun notifyScreenOn() {
        validateCurrentDay()
        checkAndInitializeCurrentDay()

        activeTime = currentTime()
    }

    fun notifyScreenOff() {
        if (activeTime == 0L) return
        val actualScreenOn = preferences.screenOn
        preferences.screenOn = actualScreenOn + (currentTime() - activeTime)
    }

    private fun validateCurrentDay() {
        if (preferences.currentDay != currentDay()) reset()
    }

    private fun checkAndInitializeCurrentDay() {
        val currentScreenOn = preferences.screenOn
        if (currentScreenOn == 0L) updateCurrentDay()
    }

    private fun reset() {
        with (preferences) {
            screenOn = 0
            currentDay = 0
        }
    }

    private fun updateCurrentDay() {
        preferences.currentDay = currentDay()
    }

    private fun currentDay() = calendarInstance().get(Calendar.DAY_OF_YEAR)
    private fun currentTime() = calendarInstance().timeInMillis

    open fun calendarInstance() = Calendar.getInstance()
}