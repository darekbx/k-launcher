package com.klauncher.api.ifmeteo

import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.*
import okhttp3.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IfWeatherTest {

    @Test
    fun currentConditions() {
        // Given
        val content = javaClass.classLoader.getResource("ifmeteo_images_response.txt")
        val bitmapContent = javaClass.classLoader.getResource("ifmeteo_image.jpg")
        val ifWeather = spy(IfWeather())

        val response = Response
                .Builder()
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("")
                .request(ifWeather.createRequest())
                .body(ResponseBody.create(MediaType.parse("application/text"), content.readBytes()))
                .build()
        val imageResponse = Response
                .Builder()
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("")
                .request(ifWeather.createRequest())
                .body(ResponseBody.create(MediaType.parse("image/jpeg"), bitmapContent.readBytes()))
                .build()
        val call = mock<Call> { on { execute() } doReturn response }
        val callImage = mock<Call> { on { execute() } doReturn imageResponse }

        doReturn(call, callImage, callImage, callImage, callImage).whenever(ifWeather).makeHttpCall(any())

        // When
        val currentConditions = ifWeather.currentConditions()

        // Then
        currentConditions
                .test()
                .assertValue { predicate ->
                    predicate.temperature is Bitmap
                }
        verify(callImage, times(4)).execute()
    }
}