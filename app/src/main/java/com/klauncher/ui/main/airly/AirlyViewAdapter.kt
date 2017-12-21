package com.klauncher.ui.main.airly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.klauncher.R
import com.klauncher.model.MapSensor

class AirlyViewAdapter(context: Context) : ArrayAdapter<MapSensor>(context, R.layout.adapter_airly_view) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
            (when (convertView) {
                null -> layoutInflater.inflate(R.layout.adapter_airly_view, parent, false)
                else -> convertView
            } as AirlySensorView).apply {
                mapSensor = getItem(position)
            }

    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }
}