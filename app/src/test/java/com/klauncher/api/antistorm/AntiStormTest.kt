package com.klauncher.api.antistorm

import org.junit.Test

import org.junit.Assert.*

class AntiStormTest {

    val antiStorm = AntiStorm()

    @Test
    fun windUrl() {
        val url = antiStorm.windUrl("folder", "file")
        assertEquals("https://antistorm.eu/archive/folder/file-radar-velocityMapImg.png", url)
    }

    @Test
    fun probabilityUrl() {
        val url = antiStorm.probabilityUrl("folder", "file")
        assertEquals("https://antistorm.eu/archive/folder/file-radar-probabilitiesImg.png", url)
    }

    @Test
    fun rainUrl() {
        val url = antiStorm.rainUrl("frontFile")
        assertEquals("https://antistorm.eu/visualPhenom/frontFile-radar-visualPhenomenon.png", url)
    }

    @Test
    fun stormUrl() {
        val url = antiStorm.stormUrl("frontFile")
        assertEquals("https://antistorm.eu/visualPhenom/frontFile-storm-visualPhenomenon.png", url)
    }
}