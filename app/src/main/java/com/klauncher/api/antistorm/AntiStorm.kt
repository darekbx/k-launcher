package com.klauncher.api.antistorm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
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

    suspend fun loadImages(): List<Bitmap>? {
        return loadImageUrls()?.let { urls ->
            urls.map { loadImage(it) }.filterNotNull()
        } ?: null
    }

    suspend fun loadImageUrls(): Array<String>? {
        val content = loadUrl(apiUrl())?.string()
        return content?.run { extractResponse(this) } ?: null
    }

    suspend fun loadImage(url: String): Bitmap? {
        return loadUrl(url)?.byteStream()?.run { BitmapFactory.decodeStream(this) } ?: null
    }

    private fun extractResponse(content: String): Array<String> {
        ApiParser()
                .parse(content)
                .takeIf { it.isValid() }
                ?.run {
                    val folderName = folderNames.first()
                    val fileName = fileNames.first()
                    val fileFrontName = fileFrontNames.first()

                    val probabilityUrl = probabilityUrl(folderName, fileName)
                    val rainUrl = rainUrl(fileFrontName)
                    val stormUrl = stormUrl(fileFrontName)

                    return arrayOf(probabilityUrl, rainUrl, stormUrl)
                }

        return emptyArray()
    }

    suspend fun loadUrl(url: String) = GlobalScope.async(Dispatchers.IO) {
        val request = buildRequest(url)
        val response = httpClient.newCall(request).execute()
        when (response.isSuccessful) {
            true -> response.body()
            else -> throw Exception(response.message())
        }
    }.await()

    private fun buildRequest(url: String): Request? {
        return Request
                .Builder()
                .url(url)
                .build()
    }

    private val httpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    fun apiUrl() = "$BASE_URL$API"
    fun windUrl(folderName: String, fileName: String) = "$BASE_URL$IMAGE_WIND".format(folderName, fileName)
    fun probabilityUrl(folderName: String, fileName: String) = "$BASE_URL$IMAGE_PROBABILITY".format(folderName, fileName)
    fun rainUrl(frontFileName: String) = "$BASE_URL$IMAGE_RAIN".format(frontFileName)
    fun stormUrl(frontFileName: String) = "$BASE_URL$IMAGE_STORM".format(frontFileName)
}