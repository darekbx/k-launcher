package com.klauncher.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.app.Fragment
import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.extensions.hide
import com.klauncher.extensions.show
import com.klauncher.external.Preferences
import com.klauncher.model.MapSensor
import com.klauncher.model.rest.ifmeteo.WeatherConditions
import com.klauncher.model.rest.zm.ActualPollution
import com.klauncher.ui.main.airly.AirlyMapViewAdapter
import com.klauncher.ui.main.airly.AirlyViewAdapter
import com.klauncher.ui.main.traffic.TrafficDrawable
import com.klauncher.ui.main.screenon.ScreenTime
import com.klauncher.ui.main.zm.PollutionView
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

    override fun displaySensors(mapSensor: MapSensor) {
        if (!isAdded) return
        with(airlyAdapter) {
            add(mapSensor)
            notifyDataSetChanged()
            airlyMap.refresh()
        }
    }

    override fun displayPollution(actualPollution: ActualPollution) {
        if (!isAdded) return
        pollutionPM10.setPollution(actualPollution.pm10, "PM  10:")
        pollutionPM25.setPollution(actualPollution.pm25, "PM 2.5:")
        pollutionInfo.text = "${actualPollution.time}\n${actualPollution.pm10.name}"
    }

    override fun displayDotCount(count: Int) {
        if (!isAdded) return
        dotCount.text = "$count"
    }

    override fun displayIfWeather(weatherConditions: WeatherConditions) {
        if (!isAdded) return
        showAllIfMeteoViews()
        if (weatherConditions.temperature is Double) {
            ifTemperature.text = "${weatherConditions.temperature}"
            ifWindChill.text = "${weatherConditions.windChill}"
            hideIfMeteoImageViews()
        } else {
            ifTemperatureImage.setImageBitmap(weatherConditions.temperature as Bitmap)
            ifWindChillImage.setImageBitmap(weatherConditions.windChill as Bitmap)
            hideIfMeteoDoubleViews()
        }
    }

    private fun hideIfMeteoDoubleViews() {
        ifTemperature.hide()
        ifWindChill.hide()
    }

    private fun hideIfMeteoImageViews() {
        ifTemperatureImage.hide()
        ifWindChillImage.hide()
    }

    private fun showAllIfMeteoViews() {
        ifTemperatureImage.show()
        ifWindChillImage.show()
        ifTemperature.show()
        ifWindChill.show()
    }

    override fun notifyError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        registerScreenBroadcast()
        return LayoutInflater.from(context).inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScreenOn()
        loadSunriseSunset()

        airlyMap.adapter = airlyAdapter
        loadData()

        val routePath = TrafficDrawable()
        drawableContainer.background = routePath

        ObjectAnimator.ofFloat(routePath, TrafficDrawable.MORPH_PROGRESS, 1f, 0f).apply {
            duration = 20000L
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
        }.start()
    }

    private fun loadData() {
        presenter.loadSensors()
        presenter.loadPollution()
        presenter.loadDotCount()
        presenter.loadIfWeather()
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

    private val dotCount: TextView by bind(R.id.dot_count)
    private val screenOnText: TextView by bind(R.id.screen_on_text)
    private val sunriseText: TextView by bind(R.id.sunrise_text)
    private val sunsetText: TextView by bind(R.id.sunset_text)
    private val ifTemperature: TextView by bind(R.id.if_temperature)
    private val ifWindChill: TextView by bind(R.id.if_wind_chill)
    private val ifTemperatureImage: ImageView by bind(R.id.if_temperature_image)
    private val ifWindChillImage: ImageView by bind(R.id.if_wind_chill_image)
    private val pollutionInfo: TextView by bind(R.id.pollution_location_info)
    private val pollutionPM10: PollutionView by bind(R.id.pollution_pm10)
    private val pollutionPM25: PollutionView by bind(R.id.pollution_pm25)
    private val airlyMap: AirlyMapViewAdapter by bind(R.id.airly_map)
    private val drawableContainer: LinearLayout by bind(R.id.drawable_container)
}