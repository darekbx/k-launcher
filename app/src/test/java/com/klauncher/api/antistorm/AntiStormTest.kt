package com.klauncher.api.antistorm

import kotlinx.coroutines.runBlocking
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
            val response = antiStorm.loadUrl(antiStorm.apiUrl())
            assertNotNull(response?.string())
            assertTrue(response?.string()?.indexOf("timestamp") == 0)
        }
    }

    @Test
    fun `Load images`() {
        runBlocking {
            val images = antiStorm.loadImages()
            assertNotNull(images)
            assertEquals(3, images?.size)
            var image1 = images?.get(0)
            var image2 = images?.get(1)
            var image3 = images?.get(2)
            assertNotNull(image1)
            assertEquals(350, image1?.width)
            assertEquals(350, image1?.height)
            assertNotNull(image2)
            assertEquals(775, image2?.width)
            assertEquals(775, image2?.height)
            assertNotNull(image3)
            assertEquals(1190, image3?.width)
            assertEquals(1190, image3?.height)
        }
    }

    @Test
    fun `Load image from url`() {
        runBlocking {
            val images = antiStorm.loadImageUrls()
            assertNotNull(images)
            val image = antiStorm.loadImage(images?.get(1) ?: "")
            assertNotNull(image)
            assertEquals(775, image?.width)
            assertEquals(775, image?.height)
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