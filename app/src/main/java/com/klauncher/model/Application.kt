package com.klauncher.model

import android.graphics.drawable.Drawable

data class Application(
        val name: String,
        val packageName: String,
        var icon: Drawable? = null,
        var clickCount: Int = 0) {

    operator fun invoke() = this.name[0]

}