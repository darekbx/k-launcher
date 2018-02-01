package com.klauncher.di

import com.klauncher.api.airly.AirlyController
import org.koin.android.module.AndroidModule

class AirlyModule: AndroidModule() {

    override fun context() = applicationContext {
        provide { AirlyController() }
    }
}