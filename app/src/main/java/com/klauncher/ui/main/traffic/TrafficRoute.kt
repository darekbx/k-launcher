package com.klauncher.ui.main.traffic

import android.graphics.Path
import android.graphics.PathMeasure

class TrafficRoute() {

    val path = Path()

    val length by lazy(LazyThreadSafetyMode.NONE) {
        with(PathMeasure()) {
            setPath(path, false)
            length
        }
    }

    fun phase(progress: Float) = progress * length * 2

    fun initializePath(points: Array<Float>) {
        with(path) {
            val first = 0;
            moveTo(points[first], points[first + 1])
            for (i in 2 until points.size step 2) {
                lineTo(points[i], points[i + 1])
            }
        }
    }
}