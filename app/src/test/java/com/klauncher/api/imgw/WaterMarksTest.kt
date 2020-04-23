package com.klauncher.api.imgw

import com.klauncher.external.Preferences
import org.junit.Test

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(sdk = [23])
@RunWith(RobolectricTestRunner::class)
class WaterMarksTest {

    @Test
    fun loadWaterMarks() {
        // Given
        val waterMarks = WaterMarks(Preferences(RuntimeEnvironment.application))

        // When
        val data = waterMarks.loadReadings()

        // Then
        assert(data != null)
        assert(data!!.size  > 10)
        assert(data!!.get(1).currentLevel > 0)
        assert(data!!.get(1).previousLevel > 0)

        // When from cache
        val dataCache = waterMarks.loadReadings()
        assert(data != null)
        assert(data!!.size  > 10)
        assert(data!!.get(1).currentLevel > 0)
        assert(data!!.get(1).previousLevel > 0)
    }
}