package com.klauncher.model.rest.zm

import android.graphics.Color
import android.text.TextUtils

data class Pollution(
        val current_value: String,
        val day_avg_value: String,
        val norm_value: String,
        val trend: String,
        val state: String,
        val name: String,
        val location: String) {

    val trendObject: Trend
        get() = Trend.valueOf(trend.toUpperCase())

    val stateObject: State
        get() = State.valueOf(state.toUpperCase())

    fun isValid() = !TextUtils.isEmpty(current_value)

    fun getColor() =
            when (stateObject) {
                State.GOOD -> Color.rgb(76, 175, 80)
                State.NOTBAD -> Color.rgb(255, 235, 59);
                State.BAD -> Color.rgb(255, 152, 0);
                State.VERYBAD -> Color.rgb(255, 87, 34);
                State.EXTREMELYBAD -> Color.rgb(186, 0, 0);
                else -> Color.rgb(76, 175, 80)
            }

    fun getArrow() =
            when (trendObject) {
                Trend.UP -> "\u2191"
                Trend.DOWN -> "\u2193"
                Trend.NOCHANGE -> "-"
            }

    enum class Trend {
        UP,
        NOCHANGE,
        DOWN
    }

    enum class State {
        GOOD,
        NOTBAD,
        BAD,
        VERYBAD,
        EXTREMELYBAD,
        HAZARDOUS
    }
}