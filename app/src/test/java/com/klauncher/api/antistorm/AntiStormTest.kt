package com.klauncher.api.antistorm

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = intArrayOf(21))
class AntiStormTest {

    val antiStorm = AntiStorm()

    @Test
    fun `Load api and check result`() {
        runBlocking {
            val response = antiStorm.loadApi()
            System.out.print(response)
            assertNotNull(response)
            assertTrue(response?.indexOf("timestamp") == 0)
        }
    }

    @Test
    fun `Validate wind url`() {
        val url = antiStorm.windUrl("folder", "file")
        assertEquals("https://antistorm.eu/archive/folder/file-radar-velocityMapImg.png", url)
    }

    @Test
    fun `Validate probability url`() {
        val url = antiStorm.probabilityUrl("folder", "file")
        assertEquals("https://antistorm.eu/archive/folder/file-radar-probabilitiesImg.png", url)
    }

    @Test
    fun `Validate rain url`() {
        val url = antiStorm.rainUrl("frontFile")
        assertEquals("https://antistorm.eu/visualPhenom/frontFile-radar-visualPhenomenon.png", url)
    }

    @Test
    fun `Validate storm url`() {
        val url = antiStorm.stormUrl("frontFile")
        assertEquals("https://antistorm.eu/visualPhenom/frontFile-storm-visualPhenomenon.png", url)
    }
}