package com.klauncher.api.antistorm

import android.graphics.Bitmap
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import java.lang.Exception

class AntiStorm {

    companion object {
        const val BASE_URL = "https://antistorm.eu"
        const val API = "/ajaxPaths.php?lastTimestamp=0&type=radar"
        const val IMAGE_WIND = "/archive/%s/%s-radar-velocityMapImg.png" // folderName, fileName
        const val IMAGE_PROBABILITY = "/archive/%s/%s-radar-probabilitiesImg.png" // folderName, fileName
        const val IMAGE_RAIN = "/visualPhenom/%s-radar-visualPhenomenon.png" // frontFileName
        const val IMAGE_STORM = "/visualPhenom/%s-storm-visualPhenomenon.png" // frontFileName
    }

    fun loadMap(): String? {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttp = OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

        val request = Request
                .Builder()
                .url(apiUrl())
                .build()

        GlobalScope.launch(Dispatchers.Main) {
            val response = okHttp.newCall(request).execute()
            when(response.isSuccessful) {
                true -> {

                    response.body()?.string()

                }
                else -> ""//throw Exception(response.message())
            }
        }
    }

    fun apiUrl() = "$BASE_URL$API"
    fun windUrl(folderName: String, fileName: String) = "$BASE_URL$IMAGE_WIND".format(folderName, fileName)
    fun probabilityUrl(folderName: String, fileName: String) = "$BASE_URL$IMAGE_PROBABILITY".format(folderName, fileName)
    fun rainUrl(frontFileName: String) = "$BASE_URL$IMAGE_RAIN".format(frontFileName)
    fun stormUrl(frontFileName: String) = "$BASE_URL$IMAGE_STORM".format(frontFileName)
}