package com.klauncher.ui.applications

import android.content.Context
import com.klauncher.external.Preferences
import java.util.*

class TimeKeeper(context: Context) {

    val OFFSET = 2 * 60 * 60 * 1000 // 2 hours

    fun canOpen(): Boolean {
        var lastOpenTime = preferences.timeKeeperTime

        if (lastOpenTime == null) {
            lastOpenTime = 0L
        }

        var canOpen = false
        var now = Calendar.getInstance().timeInMillis

        if (now - lastOpenTime > OFFSET) {
            canOpen = true
            setTime()
        }

        return canOpen;
    }

    private fun setTime() {
        preferences.timeKeeperTime = Calendar.getInstance().timeInMillis
    }

    private val preferences: Preferences by lazy { Preferences(context) }
}
