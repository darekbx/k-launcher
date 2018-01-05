package com.klauncher.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.app.Fragment
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.external.Preferences
import com.klauncher.model.MapSensor
import com.klauncher.ui.main.airly.AirlyMapViewAdapter
import com.klauncher.ui.main.airly.AirlyViewAdapter
import com.klauncher.ui.main.airly.RoutePath
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
        with(airlyAdapter) {
            add(mapSensor)
            notifyDataSetChanged()
            airlyMap.refresh()
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
        loadSunriseSunset()

        airlyMap.adapter = airlyAdapter
        presenter.loadSensors()

        val routePath = RoutePath()
        drawableContainer.background = routePath

        ObjectAnimator.ofFloat(routePath, RoutePath.MORPH_PROGRESS, 1f, 0f).apply {
            duration = 20000L
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
        }.start()
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

    private fun loadSunriseSunset() {
        with(SunriseSunset().load()) {
            sunriseText.text = getString(R.string.sunrise, first)
            sunsetText.text = getString(R.string.sunset, second)
        }
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
    private val sunriseText: TextView by bind(R.id.sunrise_text)
    private val sunsetText: TextView by bind(R.id.sunset_text)
    private val airlyMap: AirlyMapViewAdapter by bind(R.id.airly_map)
    private val drawableContainer: LinearLayout by bind(R.id.drawable_container)
}