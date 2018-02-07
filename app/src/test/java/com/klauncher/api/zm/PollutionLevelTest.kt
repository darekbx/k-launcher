package com.klauncher.api.zm

import com.nhaarman.mockito_kotlin.*
import okhttp3.*
import org.junit.Test

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PollutionLevelTest {

    @Test
    fun loadPollution() {
        // Given
        val content = javaClass.classLoader.getResource("zmdata.txt")
        val contentBytes = content.readBytes().copyOf(content.readBytes().size - 1)

        val pollutionLevel = spy(PollutionLevel())
        val response = Response
                .Builder()
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("")
                .request(pollutionLevel.createRequest())
                .body(ResponseBody.create(MediaType.parse("application/json"), contentBytes))
                .build()
        val call = mock<Call> {
            on { execute() } doReturn response
        }

        doReturn(call).whenever(pollutionLevel).createCall(any())

        // When
        val actualPollution = pollutionLevel.loadPollution()

        // Then
        actualPollution
                .test()
                .assertValue { predicate ->
                    predicate.pm10 != null
                            && predicate.pm25 != null
                            && predicate.pm10.state == "good"
                }

    }
}