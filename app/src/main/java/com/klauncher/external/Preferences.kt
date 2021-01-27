package com.klauncher.external

import android.content.Context
import android.content.ContextWrapper
import java.util.*

class Preferences(val context: Context) : ContextWrapper(context) {

    val PREFERENCES_NAME = "com.klauncher.preferences"
    val SCREEN_ON = "screen_on"
    val CURRENT_DAY = "current_day"
    val WATER_MARKS_DATE = "water_marks_date"
    val WATER_MARKS_DATA = "water_marks_data"
    val TIME_KEEPER = "time_keeper"
    val preferences = getSharedPreferences(PREFERENCES_NAME, 0)

    fun reset() = preferences.edit().clear().apply()

    var screenOn: Long
        get() = preferences.getLong(SCREEN_ON, 0L)
        set(value) = preferences.edit().putLong(SCREEN_ON, value).apply()

    var currentDay: Int
        get() = preferences.getInt(CURRENT_DAY, 0)
        set(value) = preferences.edit().putInt(CURRENT_DAY, value).apply()

    var waterMarksReadingDate: String?
        get() = preferences.getString(WATER_MARKS_DATE, null)
        set(value) = preferences.edit().putString(WATER_MARKS_DATE, value).apply()

    var waterMarksReadingData: String?
        get() = preferences.getString(WATER_MARKS_DATA, null)
        set(value) = preferences.edit().putString(WATER_MARKS_DATA, value).apply()

    var timeKeeperTime: Long
        get() = preferences.getLong(TIME_KEEPER, 0)
        set(value) = preferences.edit().putLong(TIME_KEEPER, value).apply()
}
