package com.klauncher.api.ifmeteo

import android.graphics.BitmapFactory
import com.klauncher.model.rest.ifmeteo.WeatherConditions
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

open class IfWeather {

    companion object {
        val IF_ADDRESS_BASE = "http://www.if.pw.edu.pl/~meteo/"
        val IF_ADDRESS = IF_ADDRESS_BASE + "index-mob.php"
    }

    fun currentConditions() =
            Single
                    .just(createRequest())
                    .map { request -> makeHttpCall(request).execute() }
                    .map { response -> responseToString(response) }
                    .flatMap { content ->
                        with(parser) {
                            if (checkForImages(content)) {
                                return@flatMap extractImages(content)
                            } else {
                                return@flatMap Single.just(parse(content))
                            }
                        }
                    }

    private fun IfParser.extractImages(content: String) =
            Observable
                    .fromCallable { parseImageChunks(content) }
                    .flatMapIterable { it }
                    .map { pathPart -> createRequest(IF_ADDRESS_BASE + pathPart) }
                    .map { request -> makeHttpCall(request).execute() }
                    .map { response -> responseToBitmap(response) }
                    .toList()
                    .map { list -> WeatherConditions(list[0], list[1], list[2], list[3]) }

    fun createRequest() = createRequest(IF_ADDRESS)

    private fun createRequest(address: String) = Request.Builder().url(address).build()

    open fun makeHttpCall(request: Request?) = OkHttpClient().newCall(request)

    fun responseToString(response: Response) = response.body()?.string() ?: ""

    fun responseToBitmap(response: Response) = BitmapFactory.decodeStream(response.body()?.byteStream())

    private val parser by lazy { IfParser() }
}