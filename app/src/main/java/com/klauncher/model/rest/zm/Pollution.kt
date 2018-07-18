package com.klauncher.model.rest.zm

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import kotlin.math.log

data class Pollution(
        val current_value: String,
        val day_avg_value: String,
        val norm_value: String,
        val trend: String,
        var state: String,
        val name: String,
        val location: String) {

    constructor() : this("", "", "", "", "", "", "")

    val trendObject: Trend
        get() = Trend.valueOf(trend.toUpperCase())

    val stateObject: State
        get() = State.parse(state.toUpperCase())

    fun isValid() = !TextUtils.isEmpty(current_value)

    fun getColor() =
            when (stateObject) {
                State.GOOD -> Color.rgb(76, 175, 80)
                State.NOTBAD -> Color.rgb(255, 235, 59)
                State.BAD -> Color.rgb(255, 152, 0)
                State.VERYBAD -> Color.rgb(255, 87, 34)
                State.EXTREMELYBAD -> Color.rgb(186, 0, 0)
                else -> Color.rgb(76, 175, 80)
            }

    fun getArrow() =
            when (trendObject) {
                Trend.UP -> "\u2191"
                Trend.DOWN -> "\u2193"
                else -> "-"
            }

    fun translateToPollutionColor(value: Double, step: Double): Int {
        state = when {
            value < step -> State.GOOD
            value >= step && value < step * 2 -> State.NOTBAD
            value >= step * 2 && value < step * 3 -> State.BAD
            value >= step * 3 && value < step * 4 -> State.VERYBAD
            value >= step * 4 -> State.EXTREMELYBAD
            else -> State.HAZARDOUS
        }.name
        return getColor()
    }

    enum class Trend {
        UP,
        NOCHANGE,
        DOWN,
        UNDEFINED
    }

    enum class State {
        GOOD,
        NOTBAD,
        BAD,
        VERYBAD,
        EXTREMELYBAD,
        HAZARDOUS;

        companion object {
            fun parse(value: String): State {
                if (TextUtils.isEmpty(value)) {
                    return HAZARDOUS
                }
                return State.valueOf(value)
            }
        }
    }
}