package com.klauncher.ui.main.zm

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.klauncher.model.rest.zm.Pollution

class PollutionView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {

    fun setPollution(pollution: Pollution, label: String) {
        setTextColor(pollution.getColor())
        setText("$label ${pollution.current_value}${pollution.getArrow()}")
        invalidate()
    }
}