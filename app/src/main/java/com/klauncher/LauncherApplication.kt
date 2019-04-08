package com.klauncher

import android.app.Application
import com.facebook.stetho.Stetho
import com.klauncher.di.AirlyModule
import com.klauncher.di.DefaultModule
import net.time4j.android.ApplicationStarter
import org.koin.android.ext.android.startKoin

class LauncherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        startKoin(this, listOf(AirlyModule(), DefaultModule()))
        ApplicationStarter.initialize(this, true)
    }
}