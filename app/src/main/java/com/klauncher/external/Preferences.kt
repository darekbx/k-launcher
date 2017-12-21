package com.klauncher.external

import android.content.Context
import android.content.ContextWrapper

class Preferences(val context: Context) : ContextWrapper(context) {

    val PREFERENCES_NAME = "com.klauncher.preferences"
    val SCREEN_ON = "screen_on"
    val CURRENT_DAY = "current_day"
    val preferences = getSharedPreferences(PREFERENCES_NAME, 0)

    fun reset() = preferences.edit().clear().apply()

    var screenOn: Long
        get() = preferences.getLong(SCREEN_ON, 0L)
        set(value) = preferences.edit().putLong(SCREEN_ON, value).apply()

    var currentDay: Int
        get() = preferences.getInt(CURRENT_DAY, 0)
        set(value) = preferences.edit().putInt(CURRENT_DAY, value).apply()
}