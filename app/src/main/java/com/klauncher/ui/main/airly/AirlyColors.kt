package com.klauncher.ui.main.airly

enum class AirlyColors(val color: String) {
    NO_DATA ("#aaaaaa"),
    VERY_GOOD ("#6bc926"),
    GOOD ("#d1cf1e"),
    BAD ("#efbb0f"),
    VERY_BAD ("#ef7120"),
    EXTREMELY_BAD ("#ef2a36"),
    HAZARDOUS ("#9d0028");

    companion object {
        fun from(index: Int): String = AirlyColors.values().get(index).color
    }
}