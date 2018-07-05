package com.klauncher.api.ifmeteo

import com.klauncher.model.rest.ifmeteo.WeatherConditions

class IfParser {

    companion object {
        val sectionBegin = "font-size:14pt"
        val sectionEnd = "font-size:12pt"
        val valueStart = "font-size:24pt"
        val offset = 22
        val imgOffset = 10
    }

    fun checkForImages(content: String): Boolean {
        val sectionBeginIndex = content.indexOf(sectionBegin)
        val sectionEndIndex = content.indexOf(sectionEnd, sectionBeginIndex)
        val section = content.substring(sectionBeginIndex, sectionEndIndex)

        with(section) {
            val start = indexOf(valueStart)
            val end = indexOf(' ', start)
            val value = substring(start + offset, end)
            return value.contains("img")
        }
    }

    fun parse(content: String): WeatherConditions {
        val sectionBeginIndex = content.indexOf(sectionBegin)
        val sectionEndIndex = content.indexOf(sectionEnd, sectionBeginIndex)
        val section = content.substring(sectionBeginIndex, sectionEndIndex)
        var start = 0
        var index = 0

        val chunks = ArrayList<String>(8);
        with(section) {
            while (index++ < 4) {
                start = indexOf(valueStart, start)
                val end = section.indexOf(' ', start)
                val value = section.substring(start + offset, end)
                chunks.add(value)
                start += (end - start)
            }
        }

        return weatherConditionsFromChunks(chunks)
    }

    fun weatherConditionsFromChunks(chunks: List<String>) = WeatherConditions(
            chunks.get(0).toDouble(),
            chunks.get(1).toDouble(),
            chunks.get(2).toDouble(),
            chunks.get(3).toInt()
    )

    fun parseImageChunks(content: String): List<String> {
        var sectionBeginIndex = content.indexOf(sectionBegin)
        var sectionEndIndex = content.indexOf(sectionEnd, sectionBeginIndex)
        var section = content.substring(sectionBeginIndex, sectionEndIndex)
        var start = 1
        var index = 0
        var chunks = ArrayList<String>(4)

        with(section) {
            while (index++ < 4) {
                start = indexOf(valueStart, start)
                var end = indexOf("alt", start)
                var value = substring(start + offset, end)
                value = value.substring(imgOffset)
                value = value.substring(0, value.length - 2)
                start += (end - start)
                chunks.add(value)
            }
        }

        return chunks
    }
}