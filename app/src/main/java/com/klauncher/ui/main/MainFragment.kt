package com.klauncher.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.app.Fragment
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.klauncher.DotsReceiver
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.extensions.threadToAndroid
import com.klauncher.external.Preferences
import com.klauncher.model.MapSensor
import com.klauncher.ui.main.airly.AirlyMapViewAdapter
import com.klauncher.ui.main.airly.AirlyViewAdapter
import com.klauncher.ui.main.traffic.TrafficDrawable
import com.klauncher.ui.main.screenon.ScreenTime
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class MainFragment: MainContract.View, Fragment() {

    companion object {
        val LOAD_DATA_DELAY = 3L
        val TRAFFIC_ENABLED = false
        val USE_DOT_PAD_2 = true
        val GLOBAL_TIME = "HH:mm"
    }

    val presenter = MainPresenter(this)
    var subscription: Disposable? = null
    var cachedDotCount = 0

    private val dotsReceiver  = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (hasExtra(DotsReceiver.DOTS_COUNT_KEY)) {
                    displayDotCount(intent.getIntExtra(DotsReceiver.DOTS_COUNT_KEY, -1))
                }
            }
        }
    }

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

        mapSensor.sensor?.let { sensor ->
            if (sensor.rateLimitDay == 0) {
                limitsInfo.setText("")
                return
            }
            limitsInfo.setText(getString(R.string.limits,
                    sensor.rateLimitRemainingDay, sensor.rateLimitDay,
                    sensor.rateLimitRemainingMinute, sensor.rateLimitMinute))

            val limitExceed = sensor.rateLimitRemainingDay == 0 || sensor.rateLimitRemainingMinute == 0
            limitsInfo.setTextColor(if (limitExceed) Color.RED else Color.GRAY)
        }
    }

    override fun displayDotCount(count: Int) {
        if (!isAdded) return
        cachedDotCount = count
        dotCount.text = "$count"
    }

    override fun displayAntistorm(images: List<Bitmap>) {
        if (images.size == 3) {
            with (images) {
                antistormImage1.post {
                    antistormImage1.setImageBitmap(get(0))
                    antistormImage2.setImageBitmap(get(1))
                    antistormImage3.setImageBitmap(get(2))
                }
            }
        }
    }

    override fun notifyError(error: Throwable) {
        error.printStackTrace()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerDotPadBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        context.unregisterReceiver(dotsReceiver)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        registerScreenBroadcast()
        return LayoutInflater.from(context).inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        refreshCountDown.post { refreshCountDown.start() }
        subscription = Single
                .just(1)
                .delay(LOAD_DATA_DELAY, TimeUnit.SECONDS)
                .threadToAndroid()
                .subscribe { _ -> loadOnlineData() }

        loadOfflineData()
        loadGlobalTime()

        dotCount.text = "$cachedDotCount"
    }

    override fun onPause() {
        super.onPause()
        cancelRefresh()
    }

    override fun cancelRefresh() {
        refreshCountDown.stop()
        subscription?.dispose()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScreenOn()
        loadSunriseSunset()

        refreshCountDown.reloadDelay = TimeUnit.SECONDS.toMillis(LOAD_DATA_DELAY)
        airlyMap.adapter = airlyAdapter
        loadOnlineData()

        if (TRAFFIC_ENABLED) {
            val routePath = TrafficDrawable()
            drawableContainer.background = routePath

            ObjectAnimator.ofFloat(routePath, TrafficDrawable.MORPH_PROGRESS, 1f, 0f).apply {
                duration = 20000L
                interpolator = LinearInterpolator()
                repeatCount = INFINITE
                repeatMode = RESTART
            }.start()
        }
    }

    private fun loadOfflineData() {
        if (!USE_DOT_PAD_2) {
            presenter.loadDotCount()
        }
        loadSunriseSunset()
    }

    private fun loadOnlineData() {
        airlyAdapter.clear()
        with(presenter) {
            loadSensors()
            loadAntistorm()
        }
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

    private fun registerDotPadBroadcast() {
        context.registerReceiver(dotsReceiver, IntentFilter(DotsReceiver.DOTS_FORWARD_ACTION))
    }

    private fun loadGlobalTime() {
        val currentTime = Calendar.getInstance().time

        globalTimeFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo"))
        tokioTime.setText(getString(R.string.tokio_format, globalTimeFormat.format(currentTime)))

        globalTimeFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai"))
        shanghaiTime.setText(getString(R.string.shanghai_format, globalTimeFormat.format(currentTime)))

        globalTimeFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("America/New_York"))
        nyTime.setText(getString(R.string.ny_format, globalTimeFormat.format(currentTime)))

        globalTimeFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("America/Los_Angeles"))
        laTime.setText(getString(R.string.la_format, globalTimeFormat.format(currentTime)))
    }

    private val preferences: Preferences by lazy { Preferences(context) }
    private val screenTime: ScreenTime by lazy { ScreenTime(preferences) }
    private val airlyAdapter by lazy { AirlyViewAdapter(context) }
    private val globalTimeFormat by lazy { SimpleDateFormat(GLOBAL_TIME) }

    private val dotCount: TextView by bind(R.id.dot_count)
    private val screenOnText: TextView by bind(R.id.screen_on_text)
    private val sunriseText: TextView by bind(R.id.sunrise_text)
    private val sunsetText: TextView by bind(R.id.sunset_text)
    private val airlyMap: AirlyMapViewAdapter by bind(R.id.airly_map)
    private val limitsInfo: TextView by bind(R.id.limits_text)
    private val drawableContainer: LinearLayout by bind(R.id.drawable_container)
    private val refreshCountDown: RefreshCountDown by bind(R.id.refresh_count_down)
    private val antistormImage1: ImageView by bind(R.id.antistorm_image_1)
    private val antistormImage2: ImageView by bind(R.id.antistorm_image_2)
    private val antistormImage3: ImageView by bind(R.id.antistorm_image_3)

    private val tokioTime: TextView by bind(R.id.tokio_text)
    private val shanghaiTime: TextView by bind(R.id.shanghai_text)
    private val nyTime: TextView by bind(R.id.ny_text)
    private val laTime: TextView by bind(R.id.la_text)
}