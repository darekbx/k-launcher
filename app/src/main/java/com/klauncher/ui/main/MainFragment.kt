package com.klauncher.ui.main

import android.app.Fragment
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.external.Preferences
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.SensorError
import com.klauncher.ui.main.airly.AirlyMapViewAdapter
import com.klauncher.ui.main.airly.AirlyViewAdapter
import com.klauncher.ui.main.screenon.ScreenTime
import java.sql.Date
import java.text.SimpleDateFormat

class MainFragment: MainContract.View, Fragment() {

    val presenter = MainPresenter(this)

    val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    screenTime.notifyScreenOn()
                    updateScreenOn()
                }
                Intent.ACTION_SCREEN_OFF -> screenTime.notifyScreenOff()
            }
        }
    }

    override fun refreshSensor(mapSensor: MapSensor) {
        when (mapSensor.sensor) {
            is SensorError -> { }
            else -> {
                with(airlyAdapter) {
                    Log.v("------", "add sensor ${mapSensor.name}")
                    add(mapSensor)
                    notifyDataSetChanged()
                    airlyMap.refresh()
                }
            }
        }
    }

    override fun notifyError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        registerScreenBroadcast()
        return LayoutInflater.from(context).inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScreenOn()

        airlyMap.adapter = airlyAdapter
        presenter.loadSensors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context.unregisterReceiver(screenReceiver)
    }

    private fun updateScreenOn() {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val formatted = simpleDateFormat.format(Date(preferences.screenOn - 1000 * 60 * 60))
        screenOnText.text = getString(R.string.screen_on, formatted)
    }

    private fun registerScreenBroadcast() {
        context.registerReceiver(screenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    private val preferences: Preferences by lazy { Preferences(context) }
    private val screenTime: ScreenTime by lazy { ScreenTime(preferences) }
    private val airlyAdapter by lazy { AirlyViewAdapter(context) }

    private val screenOnText: TextView by bind(R.id.screen_on_text)
    private val airlyMap: AirlyMapViewAdapter by bind(R.id.airly_map)
}