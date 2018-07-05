package com.klauncher.di

import com.klauncher.api.dotpad.DotsCount
import org.koin.android.module.AndroidModule

class DefaultModule: AndroidModule() {

    override fun context() = applicationContext {
        provide { DotsCount() }
    }
}