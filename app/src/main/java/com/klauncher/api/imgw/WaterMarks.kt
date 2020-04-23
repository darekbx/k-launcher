package com.klauncher.api.imgw

import com.google.gson.Gson
import com.klauncher.external.Preferences
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class WaterMarks constructor(val preferences: Preferences) {

    companion object {
        val ENDPOINT = "http://instytutmeteo.pl/rivers/show-watermarks"
        val RIVER_ID_KEY = "river_id"

        val WISLA_ID = 2
    }

    class Entry(val name: String, val currentLevel: Int, val previousLevel: Int)

    fun loadReadings(): List<Entry>? {
        val currentDate = currentDate()
        return when (currentDate == preferences.waterMarksReadingDate) {
            true -> loadFromCache()
            else -> readFromApi()
        }
    }

    private fun loadFromCache(): List<Entry>? {
        val data = preferences.waterMarksReadingData
        return data?.let { mapDataToEntries(data) } ?: null
    }

    private fun readFromApi(): List<Entry>? {
        try {
            loadWaterMarks(WISLA_ID)?.let { items ->
                val data = items
                        .filter { it.size == 9 }
                        .filter { it[3].toIntOrNull() != null && it[6].toIntOrNull() != null }
                        .map {
                            var key = it[0]
                            if (it[0].length >= 4) {
                               key = it[0].substring(0, 4)
                            }
                            "${key}|${it[3]}|${it[6]}" }
                        .joinToString(",")

                saveReadings(data)
                return mapDataToEntries(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun mapDataToEntries(data: String) =
            data.split(",").map {
                val chunks = it.split("|")
                Entry(chunks[0], chunks[1].toInt(), chunks[2].toInt())
            }

    private fun saveReadings(data: String) {
        preferences.waterMarksReadingDate = currentDate()
        preferences.waterMarksReadingData = data
    }

    private fun currentDate(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy:MM:dd")
        return simpleDateFormat.format(Date())
    }

    private fun loadWaterMarks(riverId: Int): List<List<String>>? {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
        val body = FormBody.Builder().add(RIVER_ID_KEY, "$riverId").build()
        val request = Request.Builder()
                .url(ENDPOINT)
                .post(body)
                .build()
        try {
            val response = client.newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                val json = response.body()?.string()
                val data = Gson().fromJson<List<Any>>(json, List::class.java)
                val html = data.get(0).toString()
                return parseHtml(html)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun parseHtml(html: String): List<List<String>> {
        val trPattern = Pattern.compile("<tr>(.*?)</tr>")
        val tdPattern = Pattern.compile("<td(.*?)>([^<]+)</td>")
        val valuePattern = Pattern.compile("<(.*?)>(.+?)</(.*?)>", Pattern.DOTALL)
        val trMatcher = trPattern.matcher(html)

        val output = mutableListOf<List<String>>()

        while (trMatcher.find()) {
            var tr = trMatcher.group()
            tr = tr.replace("></", ">-</")
            val tdMatcher = tdPattern.matcher(tr)
            val inner = mutableListOf<String>()

            while (tdMatcher.find()) {
                val td = tdMatcher.group()
                var valueMatcher = valuePattern.matcher(td)

                if (valueMatcher.find()) {
                    val value = valueMatcher.group()
                    var valueMatcherInner = valuePattern.matcher(value)

                    if (valueMatcherInner.find()) {
                        var innerValue = valueMatcherInner.group(2)
                        val gtPosition = innerValue.indexOf(">")
                        if (innerValue.indexOf(">") > 1) {
                            innerValue = innerValue.substring(gtPosition + 1)
                        }
                        inner.add(innerValue)
                    } else {
                        inner.add(value)
                    }
                }
            }
            output.add(inner)
        }
        return output
    }
}