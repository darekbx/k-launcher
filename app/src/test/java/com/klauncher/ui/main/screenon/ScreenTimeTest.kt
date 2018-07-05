package com.klauncher.ui.main.screenon

import com.klauncher.external.Preferences
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*
import java.util.concurrent.TimeUnit

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ScreenTimeTest {

    private data class Result(val preferences: Preferences, val screenTime: ScreenTime, val calendar: Calendar)

    @Before
    fun setUp() {
        Preferences(RuntimeEnvironment.application).reset()
    }

    @Test
    fun first_run() {
        // Given
        val (preferences, screenTime, calendar) = prepareTest()

        // When
        calendar.set(Calendar.MINUTE, 0)

        screenTime.notifyScreenOn()
        calendar.set(Calendar.MINUTE, 1)
        screenTime.notifyScreenOff()

        screenTime.notifyScreenOn()
        calendar.set(Calendar.MINUTE, 4)
        screenTime.notifyScreenOff()

        // Then
        assertEquals(316, preferences.currentDay)
        assertEquals(TimeUnit.MINUTES.toMillis(4), preferences.screenOn)
    }

    @Test
    fun nexy_day() {
        // Given
        val (preferences, screenTime, calendar) = prepareTest()

        // Next day
        // When
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.DAY_OF_YEAR, 317)
        screenTime.notifyScreenOn()
        calendar.set(Calendar.MINUTE, 2)
        screenTime.notifyScreenOff()

        // Then
        assertEquals(317, preferences.currentDay)
        assertEquals(TimeUnit.MINUTES.toMillis(2), preferences.screenOn)
    }

    private fun prepareTest(): Result {
        val preferences = Preferences(RuntimeEnvironment.application)
        var screenTime = spy(ScreenTime(preferences))
        val calendar = Calendar.getInstance().apply {
            set(2017, 10, 12, 0, 0, 0)
        }
        doReturn(calendar).whenever(screenTime).calendarInstance()
        return Result(preferences, screenTime, calendar)
    }
}