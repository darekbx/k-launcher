package com.klauncher

import android.app.Application
import com.klauncher.di.AirlyModule
import com.klauncher.di.DefaultModule
import net.time4j.android.ApplicationStarter
import org.koin.android.ext.android.startKoin

class LauncherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(AirlyModule(), DefaultModule()))
        ApplicationStarter.initialize(this, true)
    }
}